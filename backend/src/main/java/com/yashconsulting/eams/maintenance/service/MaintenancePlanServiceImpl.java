package com.yashconsulting.eams.maintenance.service;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.exception.MaintenancePlanCodeAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanCreateRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanSearchRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanUpdateRequest;
import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import com.yashconsulting.eams.maintenance.mapper.MaintenancePlanMapper;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.maintenance.specification.MaintenancePlanSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yashconsulting.eams.maintenance.dto.MaintenanceDashboardResponse;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenancePlanServiceImpl implements MaintenancePlanService {

    private final MaintenancePlanRepository maintenancePlanRepository;
    private final AssetRepository assetRepository;
    private final MaintenancePlanMapper maintenancePlanMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MaintenancePlanResponse createMaintenancePlan(MaintenancePlanCreateRequest request) {
        log.info("Creating new maintenance plan with code: {}", request.getPlanCode());

        // Validate asset exists
        if (!assetRepository.existsById(request.getAssetId())) {
            throw new ResourceNotFoundException("Asset not found with ID: " + request.getAssetId());
        }

        String planCode = request.getPlanCode().trim().toUpperCase(Locale.ROOT);
        request.setPlanCode(planCode);

        if (maintenancePlanRepository.existsByPlanCode(planCode)) {
            throw new MaintenancePlanCodeAlreadyExistsException("Maintenance plan code " + planCode + " is already registered");
        }

        MaintenancePlan plan = maintenancePlanMapper.toEntity(request);
        MaintenancePlan saved = maintenancePlanRepository.save(plan);
        return maintenancePlanMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MaintenancePlanResponse updateMaintenancePlan(Long id, MaintenancePlanUpdateRequest request) {
        log.info("Updating maintenance plan with ID: {}", id);
        MaintenancePlan plan = getPlanByIdOrThrow(id);

        if (request.getStatus() != null && request.getStatus() != plan.getStatus()) {
            validateStatusTransition(plan.getStatus(), request.getStatus());
            plan.setStatus(request.getStatus());
        }

        maintenancePlanMapper.updateEntity(request, plan);
        MaintenancePlan updated = maintenancePlanRepository.save(plan);
        return maintenancePlanMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenancePlanResponse getMaintenancePlanById(Long id) {
        log.info("Fetching maintenance plan with ID: {}", id);
        MaintenancePlan plan = getPlanByIdOrThrow(id);
        return maintenancePlanMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenancePlanResponse> getAllMaintenancePlans(Pageable pageable, boolean includeInactive) {
        log.info("Listing all maintenance plans. Include inactive: {}", includeInactive);
        Page<MaintenancePlan> page = includeInactive
                ? maintenancePlanRepository.findAll(pageable)
                : maintenancePlanRepository.findAllByActiveTrue(pageable);
        return page.map(maintenancePlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenancePlanResponse> searchMaintenancePlans(MaintenancePlanSearchRequest request) {
        log.info("Searching maintenance plans dynamically");
        Specification<MaintenancePlan> spec = MaintenancePlanSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<MaintenancePlan> page = maintenancePlanRepository.findAll(spec, pageable);
        return page.map(maintenancePlanMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteMaintenancePlan(Long id) {
        log.info("Soft deleting maintenance plan with ID: {}", id);
        MaintenancePlan plan = getPlanByIdOrThrow(id);
        plan.setActive(false);
        maintenancePlanRepository.save(plan);
    }

    @Override
    @Transactional
    public MaintenancePlanResponse completeMaintenancePlan(Long id, java.time.LocalDate completionDate) {
        log.info("Completing maintenance plan with ID: {} on date: {}", id, completionDate);
        MaintenancePlan plan = getPlanByIdOrThrow(id);

        validateStatusTransition(plan.getStatus(), MaintenanceStatus.COMPLETED);

        java.time.LocalDate actualCompletionDate = completionDate != null ? completionDate : java.time.LocalDate.now();
        plan.setLastMaintenanceDate(actualCompletionDate);

        java.time.LocalDate nextDate = calculateNextMaintenanceDate(actualCompletionDate, plan.getFrequencyType(), plan.getFrequencyValue());
        plan.setNextMaintenanceDate(nextDate);

        // Reschedule transitions back to SCHEDULED
        plan.setStatus(MaintenanceStatus.SCHEDULED);

        MaintenancePlan updated = maintenancePlanRepository.save(plan);
        return maintenancePlanMapper.toResponse(updated);
    }

    public java.time.LocalDate calculateNextMaintenanceDate(java.time.LocalDate completionDate, FrequencyType frequencyType, int frequencyValue) {
        if (completionDate == null || frequencyType == null) {
            throw new IllegalArgumentException("Completion date and frequency type must not be null");
        }
        if (frequencyValue <= 0) {
            throw new IllegalArgumentException("Frequency value must be a positive integer");
        }
        switch (frequencyType) {
            case DAILY:
                return completionDate.plusDays(frequencyValue);
            case WEEKLY:
                return completionDate.plusWeeks(frequencyValue);
            case MONTHLY:
                return completionDate.plusMonths(frequencyValue);
            case YEARLY:
                return completionDate.plusYears(frequencyValue);
            default:
                throw new IllegalArgumentException("Unsupported frequency type: " + frequencyType);
        }
    }

    private void validateStatusTransition(MaintenanceStatus currentStatus, MaintenanceStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }
        boolean valid = false;
        switch (currentStatus) {
            case SCHEDULED:
                valid = newStatus == MaintenanceStatus.IN_PROGRESS || newStatus == MaintenanceStatus.OVERDUE || newStatus == MaintenanceStatus.CANCELLED || newStatus == MaintenanceStatus.COMPLETED;
                break;
            case IN_PROGRESS:
                valid = newStatus == MaintenanceStatus.COMPLETED || newStatus == MaintenanceStatus.CANCELLED;
                break;
            case OVERDUE:
                valid = newStatus == MaintenanceStatus.IN_PROGRESS || newStatus == MaintenanceStatus.CANCELLED || newStatus == MaintenanceStatus.COMPLETED;
                break;
            case COMPLETED:
                valid = newStatus == MaintenanceStatus.SCHEDULED;
                break;
            case CANCELLED:
                valid = newStatus == MaintenanceStatus.SCHEDULED;
                break;
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid state transition from " + currentStatus + " to " + newStatus);
        }
    }

    @Override
    @Transactional
    public MaintenanceDashboardResponse getMaintenanceDashboard() {
        log.info("Fetching maintenance metrics dashboard data");
        checkOverdueMaintenance();

        LocalDate today = LocalDate.now();
        LocalDate endOf30Days = today.plusDays(30);

        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> statusCountsRaw = maintenancePlanRepository.countByStatus();
        Map<String, Long> countByStatus = statusCountsRaw.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));

        List<Object[]> priorityCountsRaw = maintenancePlanRepository.countByPriority();
        Map<String, Long> countByPriority = priorityCountsRaw.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));

        List<MaintenancePlanResponse> upcoming = maintenancePlanRepository.findUpcomingMaintenance(today, endOf30Days).stream()
                .map(maintenancePlanMapper::toResponse)
                .collect(Collectors.toList());

        List<MaintenancePlanResponse> overdue = maintenancePlanRepository.findOverdueMaintenance(today).stream()
                .map(maintenancePlanMapper::toResponse)
                .collect(Collectors.toList());

        List<MaintenancePlanResponse> completedThisMonth = maintenancePlanRepository.findCompletedWithinPeriod(startOfMonth, endOfMonth).stream()
                .map(maintenancePlanMapper::toResponse)
                .collect(Collectors.toList());

        return MaintenanceDashboardResponse.builder()
                .upcomingMaintenance(upcoming)
                .overdueMaintenance(overdue)
                .completedThisMonth(completedThisMonth)
                .countByStatus(countByStatus)
                .countByPriority(countByPriority)
                .build();
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkOverdueMaintenance() {
        log.info("Checking for overdue maintenance plans");
        LocalDate today = LocalDate.now();
        List<MaintenancePlan> overduePlans = maintenancePlanRepository.findOverdueMaintenance(today);
        for (MaintenancePlan plan : overduePlans) {
            if (plan.getStatus() != MaintenanceStatus.OVERDUE) {
                plan.setStatus(MaintenanceStatus.OVERDUE);
                maintenancePlanRepository.save(plan);
                eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.MaintenanceOverdueEvent(
                        plan.getId(), plan.getPlanCode(), plan.getPlanName()));
            }
        }
    }

    private MaintenancePlan getPlanByIdOrThrow(Long id) {
        return maintenancePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance plan not found with ID: " + id));
    }
}
