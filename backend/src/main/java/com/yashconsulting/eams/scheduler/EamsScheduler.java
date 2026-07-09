package com.yashconsulting.eams.scheduler;

import com.yashconsulting.eams.email.service.EmailService;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import com.yashconsulting.eams.notification.event.LowStockEvent;
import com.yashconsulting.eams.notification.event.MaintenanceOverdueEvent;
import com.yashconsulting.eams.notification.service.NotificationService;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized scheduler for all EAMS scheduled jobs.
 * Cron expressions are externalized via application.properties.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EamsScheduler {

    private final MaintenancePlanRepository maintenancePlanRepository;
    private final WorkOrderRepository workOrderRepository;
    private final SparePartRepository sparePartRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${eams.scheduler.maintenance-reminder-days:7}")
    private int maintenanceReminderDays;

    // ========================================================
    // MIDNIGHT JOBS
    // ========================================================

    @Scheduled(cron = "${eams.scheduler.cron.midnight:0 0 0 * * ?}")
    @Transactional
    public void midnightJobs() {
        log.info("=== MIDNIGHT SCHEDULER START ===");
        markOverdueMaintenance();
        markOverdueWorkOrders();
        generateMaintenanceReminderNotifications();
        generateMaintenanceReminderEmails();
        log.info("=== MIDNIGHT SCHEDULER END ===");
    }

    // ========================================================
    // HOURLY JOBS
    // ========================================================

    @Scheduled(cron = "${eams.scheduler.cron.hourly:0 0 * * * ?}")
    @Transactional
    public void hourlyJobs() {
        log.info("=== HOURLY SCHEDULER START ===");
        checkLowStock();
        checkUpcomingMaintenance();
        log.info("=== HOURLY SCHEDULER END ===");
    }

    // ========================================================
    // MIDNIGHT: Mark overdue maintenance plans
    // ========================================================

    private void markOverdueMaintenance() {
        log.info("Checking for overdue maintenance plans");
        LocalDate today = LocalDate.now();
        List<MaintenancePlan> overduePlans = maintenancePlanRepository.findOverdueMaintenance(today);

        int count = 0;
        for (MaintenancePlan plan : overduePlans) {
            if (plan.getStatus() != MaintenanceStatus.OVERDUE) {
                plan.setStatus(MaintenanceStatus.OVERDUE);
                maintenancePlanRepository.save(plan);
                eventPublisher.publishEvent(new MaintenanceOverdueEvent(
                        plan.getId(), plan.getPlanCode(), plan.getPlanName()));
                count++;
            }
        }
        log.info("Marked {} maintenance plans as overdue", count);
    }

    // ========================================================
    // MIDNIGHT: Mark overdue work orders
    // ========================================================

    private void markOverdueWorkOrders() {
        log.info("Checking for overdue work orders");
        LocalDate today = LocalDate.now();
        List<WorkOrder> overdueOrders = workOrderRepository.findOverdueWorkOrders(today);

        List<User> recipients = getAdminAndManagerRecipients();
        int count = 0;

        for (WorkOrder wo : overdueOrders) {
            for (User user : recipients) {
                NotificationCreateRequest request = NotificationCreateRequest.builder()
                        .title("Work Order Overdue")
                        .message(String.format("Work order %s is past its scheduled date.", wo.getWorkOrderNumber()))
                        .notificationType(NotificationType.WORK_ORDER)
                        .priority(NotificationPriority.HIGH)
                        .recipientUserId(user.getId())
                        .referenceType("WORK_ORDER")
                        .referenceId(wo.getId())
                        .build();
                notificationService.createNotification(request);
            }
            count++;
        }
        log.info("Generated overdue notifications for {} work orders", count);
    }

    // ========================================================
    // MIDNIGHT: Generate maintenance reminder notifications
    // ========================================================

    private void generateMaintenanceReminderNotifications() {
        log.info("Generating maintenance reminder notifications");
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(maintenanceReminderDays);

        List<MaintenancePlan> upcomingPlans = maintenancePlanRepository.findUpcomingMaintenance(today, reminderDate);
        List<User> recipients = getAdminAndManagerRecipients();

        int count = 0;
        for (MaintenancePlan plan : upcomingPlans) {
            if (plan.getStatus() == MaintenanceStatus.SCHEDULED) {
                for (User user : recipients) {
                    NotificationCreateRequest request = NotificationCreateRequest.builder()
                            .title("Maintenance Reminder")
                            .message(String.format("Maintenance plan %s (%s) is due on %s.",
                                    plan.getPlanCode(), plan.getPlanName(), plan.getNextMaintenanceDate()))
                            .notificationType(NotificationType.MAINTENANCE)
                            .priority(NotificationPriority.MEDIUM)
                            .recipientUserId(user.getId())
                            .referenceType("MAINTENANCE")
                            .referenceId(plan.getId())
                            .build();
                    notificationService.createNotification(request);
                }
                count++;
            }
        }
        log.info("Generated reminder notifications for {} upcoming maintenance plans", count);
    }

    // ========================================================
    // MIDNIGHT: Generate maintenance reminder emails
    // ========================================================

    private void generateMaintenanceReminderEmails() {
        log.info("Generating maintenance reminder emails");
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(maintenanceReminderDays);

        List<MaintenancePlan> upcomingPlans = maintenancePlanRepository.findUpcomingMaintenance(today, reminderDate);
        List<User> recipients = getAdminAndManagerRecipients();

        int count = 0;
        for (MaintenancePlan plan : upcomingPlans) {
            if (plan.getStatus() == MaintenanceStatus.SCHEDULED) {
                for (User user : recipients) {
                    emailService.sendMaintenanceReminderEmail(
                            user.getEmail(),
                            plan.getPlanCode(),
                            plan.getPlanName(),
                            plan.getNextMaintenanceDate().toString()
                    );
                }
                count++;
            }
        }
        log.info("Sent reminder emails for {} upcoming maintenance plans", count);
    }

    // ========================================================
    // HOURLY: Check low stock
    // ========================================================

    private void checkLowStock() {
        log.info("Checking low stock items");
        List<SparePart> lowStockItems = sparePartRepository.findAllLowStockItems();

        int count = 0;
        for (SparePart part : lowStockItems) {
            eventPublisher.publishEvent(new LowStockEvent(
                    part.getId(),
                    part.getPartNumber(),
                    part.getPartName(),
                    part.getCurrentStock(),
                    part.getMinimumStock()
            ));
            count++;
        }
        log.info("Published low stock events for {} items", count);
    }

    // ========================================================
    // HOURLY: Check upcoming maintenance
    // ========================================================

    private void checkUpcomingMaintenance() {
        log.info("Checking upcoming maintenance within {} days", maintenanceReminderDays);
        LocalDate today = LocalDate.now();
        LocalDate upcoming = today.plusDays(maintenanceReminderDays);

        List<MaintenancePlan> upcomingPlans = maintenancePlanRepository.findUpcomingMaintenance(today, upcoming);
        List<User> recipients = getAdminAndManagerRecipients();

        int count = 0;
        for (MaintenancePlan plan : upcomingPlans) {
            if (plan.getStatus() == MaintenanceStatus.SCHEDULED) {
                for (User user : recipients) {
                    NotificationCreateRequest request = NotificationCreateRequest.builder()
                            .title("Upcoming Maintenance")
                            .message(String.format("Maintenance plan %s (%s) is due on %s.",
                                    plan.getPlanCode(), plan.getPlanName(), plan.getNextMaintenanceDate()))
                            .notificationType(NotificationType.MAINTENANCE)
                            .priority(NotificationPriority.MEDIUM)
                            .recipientUserId(user.getId())
                            .referenceType("MAINTENANCE")
                            .referenceId(plan.getId())
                            .build();
                    notificationService.createNotification(request);
                }
                count++;
            }
        }
        log.info("Generated upcoming maintenance notifications for {} plans", count);
    }

    // ========================================================
    // Helper methods
    // ========================================================

    private List<User> getAdminAndManagerRecipients() {
        List<User> recipients = new ArrayList<>();
        recipients.addAll(userRepository.findAllByRoleAndActiveTrue(Role.ADMIN));
        recipients.addAll(userRepository.findAllByRoleAndActiveTrue(Role.MANAGER));
        return recipients;
    }
}
