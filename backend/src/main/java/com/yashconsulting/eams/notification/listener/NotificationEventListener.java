package com.yashconsulting.eams.notification.listener;

import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import com.yashconsulting.eams.notification.event.*;
import com.yashconsulting.eams.notification.service.NotificationService;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleAssetAssigned(AssetAssignedEvent event) {
        log.info("Asynchronously handling AssetAssignedEvent for asset ID: {}", event.getAssetId());

        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .title("Asset Assigned")
                .message(String.format("Asset %s has been assigned to you.", event.getAssetCode()))
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.MEDIUM)
                .recipientUserId(event.getEmployeeId())
                .referenceType("ASSET")
                .referenceId(event.getAssetId())
                .build();

        notificationService.createNotification(request);
    }

    @Async
    @EventListener
    public void handleMaintenanceOverdue(MaintenanceOverdueEvent event) {
        log.info("Asynchronously handling MaintenanceOverdueEvent for plan Code: {}", event.getPlanCode());

        List<User> recipients = getAdminAndManagerRecipients();
        for (User user : recipients) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Maintenance Overdue")
                    .message(String.format("Maintenance plan %s (%s) is overdue.", event.getPlanCode(), event.getPlanName()))
                    .notificationType(NotificationType.MAINTENANCE)
                    .priority(NotificationPriority.HIGH)
                    .recipientUserId(user.getId())
                    .referenceType("MAINTENANCE")
                    .referenceId(event.getPlanId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    @Async
    @EventListener
    public void handleWorkOrderCreated(WorkOrderCreatedEvent event) {
        log.info("Asynchronously handling WorkOrderCreatedEvent for WO: {}", event.getWorkOrderNumber());

        Long recipientId = findRecipientForTechnician(event.getAssignedTechnician());
        if (recipientId != null) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Work Order Created")
                    .message(String.format("Work order %s has been created.", event.getWorkOrderNumber()))
                    .notificationType(NotificationType.WORK_ORDER)
                    .priority(NotificationPriority.MEDIUM)
                    .recipientUserId(recipientId)
                    .referenceType("WORK_ORDER")
                    .referenceId(event.getWorkOrderId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    @Async
    @EventListener
    public void handleWorkOrderCompleted(WorkOrderCompletedEvent event) {
        log.info("Asynchronously handling WorkOrderCompletedEvent for WO: {}", event.getWorkOrderNumber());

        Long recipientId = findRecipientForTechnician(event.getAssignedTechnician());
        if (recipientId != null) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Work Order Completed")
                    .message(String.format("Work order %s has been completed.", event.getWorkOrderNumber()))
                    .notificationType(NotificationType.WORK_ORDER)
                    .priority(NotificationPriority.LOW)
                    .recipientUserId(recipientId)
                    .referenceType("WORK_ORDER")
                    .referenceId(event.getWorkOrderId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    @Async
    @EventListener
    public void handleLowStock(LowStockEvent event) {
        log.info("Asynchronously handling LowStockEvent for part: {}", event.getPartNumber());

        List<User> recipients = getAdminAndManagerRecipients();
        for (User user : recipients) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Low Stock Alert")
                    .message(String.format("Spare part %s (%s) is low on stock. Current: %d, Minimum: %d",
                            event.getPartNumber(), event.getPartName(), event.getCurrentStock(), event.getMinimumStock()))
                    .notificationType(NotificationType.INVENTORY)
                    .priority(NotificationPriority.HIGH)
                    .recipientUserId(user.getId())
                    .referenceType("SPARE_PART")
                    .referenceId(event.getSparePartId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    @Async
    @EventListener
    public void handlePurchaseOrderApproved(PurchaseOrderApprovedEvent event) {
        log.info("Asynchronously handling PurchaseOrderApprovedEvent for PO: {}", event.getPoNumber());

        Long recipientId = findRecipientForCreatedBy(event.getCreatedBy());
        if (recipientId != null) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Purchase Order Approved")
                    .message(String.format("Purchase order %s has been approved.", event.getPoNumber()))
                    .notificationType(NotificationType.PURCHASE_ORDER)
                    .priority(NotificationPriority.MEDIUM)
                    .recipientUserId(recipientId)
                    .referenceType("PURCHASE_ORDER")
                    .referenceId(event.getPoId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    @Async
    @EventListener
    public void handlePurchaseOrderReceived(PurchaseOrderReceivedEvent event) {
        log.info("Asynchronously handling PurchaseOrderReceivedEvent for PO: {}", event.getPoNumber());

        Long recipientId = findRecipientForCreatedBy(event.getCreatedBy());
        if (recipientId != null) {
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .title("Purchase Order Received")
                    .message(String.format("Purchase order %s has been received. Inventory has been updated.", event.getPoNumber()))
                    .notificationType(NotificationType.PURCHASE_ORDER)
                    .priority(NotificationPriority.LOW)
                    .recipientUserId(recipientId)
                    .referenceType("PURCHASE_ORDER")
                    .referenceId(event.getPoId())
                    .build();

            notificationService.createNotification(request);
        }
    }

    private List<User> getAdminAndManagerRecipients() {
        List<User> recipients = new ArrayList<>();
        recipients.addAll(userRepository.findAllByRoleAndActiveTrue(Role.ADMIN));
        recipients.addAll(userRepository.findAllByRoleAndActiveTrue(Role.MANAGER));
        return recipients;
    }

    private Long findRecipientForTechnician(String assignedTechnician) {
        if (assignedTechnician == null || assignedTechnician.isBlank()) {
            return getFallbackUserId();
        }
        var userOpt = userRepository.findByEmail(assignedTechnician.trim());
        if (userOpt.isPresent()) {
            return userOpt.get().getId();
        }
        String[] parts = assignedTechnician.trim().split("\\s+");
        if (parts.length > 0) {
            String term = parts[0];
            List<User> users = userRepository.findAll();
            for (User u : users) {
                if (u.getFirstName().equalsIgnoreCase(term) || u.getLastName().equalsIgnoreCase(term)) {
                    return u.getId();
                }
            }
        }
        return getFallbackUserId();
    }

    private Long findRecipientForCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank() || "SYSTEM".equalsIgnoreCase(createdBy)) {
            return getFallbackUserId();
        }
        return userRepository.findByEmail(createdBy.trim())
                .map(User::getId)
                .orElseGet(this::getFallbackUserId);
    }

    private Long getFallbackUserId() {
        return userRepository.findAll().stream()
                .map(User::getId)
                .findFirst()
                .orElse(null);
    }
}
