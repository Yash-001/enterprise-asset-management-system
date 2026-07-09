package com.yashconsulting.eams.workorder.service;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.exception.WorkOrderNumberAlreadyExistsException;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderResponse;
import com.yashconsulting.eams.workorder.dto.WorkOrderSearchRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.workorder.mapper.WorkOrderMapper;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
import com.yashconsulting.eams.workorder.specification.WorkOrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final AssetRepository assetRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final WorkOrderMapper workOrderMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public WorkOrderResponse createWorkOrder(WorkOrderCreateRequest request) {
        log.info("Creating new work order: {}", request.getWorkOrderNumber());

        // Validate asset exists
        if (!assetRepository.existsById(request.getAssetId())) {
            throw new ResourceNotFoundException("Asset not found with ID: " + request.getAssetId());
        }

        // Validate maintenance plan exists if provided
        if (request.getMaintenancePlanId() != null && !maintenancePlanRepository.existsById(request.getMaintenancePlanId())) {
            throw new ResourceNotFoundException("Maintenance plan not found with ID: " + request.getMaintenancePlanId());
        }

        // Validate work order number uniqueness
        String workOrderNumber = request.getWorkOrderNumber().trim().toUpperCase(Locale.ROOT);
        request.setWorkOrderNumber(workOrderNumber);

        if (workOrderRepository.existsByWorkOrderNumber(workOrderNumber)) {
            throw new WorkOrderNumberAlreadyExistsException("Work order number " + workOrderNumber + " already exists");
        }

        WorkOrder entity = workOrderMapper.toEntity(request);
        WorkOrder saved = workOrderRepository.save(entity);
        
        eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.WorkOrderCreatedEvent(
                saved.getId(), saved.getWorkOrderNumber(), saved.getAssignedTechnician()));

        return workOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WorkOrderResponse updateWorkOrder(Long id, WorkOrderUpdateRequest request) {
        log.info("Updating work order with ID: {}", id);
        WorkOrder entity = getWorkOrderOrThrow(id);
        com.yashconsulting.eams.workorder.entity.WorkOrderStatus oldStatus = entity.getStatus();

        // Validate state transition if status changes
        if (request.getStatus() != null && request.getStatus() != entity.getStatus()) {
            validateStatusTransition(entity.getStatus(), request.getStatus());
        }

        workOrderMapper.updateEntity(request, entity);
        WorkOrder updated = workOrderRepository.save(entity);

        if (oldStatus != com.yashconsulting.eams.workorder.entity.WorkOrderStatus.COMPLETED 
                && updated.getStatus() == com.yashconsulting.eams.workorder.entity.WorkOrderStatus.COMPLETED) {
            eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.WorkOrderCompletedEvent(
                    updated.getId(), updated.getWorkOrderNumber(), updated.getAssignedTechnician()));
        }

        return workOrderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(Long id) {
        log.info("Fetching work order with ID: {}", id);
        WorkOrder entity = getWorkOrderOrThrow(id);
        return workOrderMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderResponse> getAllWorkOrders(Pageable pageable, boolean includeInactive) {
        log.info("Listing all work orders. Include inactive: {}", includeInactive);
        Page<WorkOrder> page = includeInactive
                ? workOrderRepository.findAll(pageable)
                : workOrderRepository.findAllByActiveTrue(pageable);
        return page.map(workOrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderResponse> searchWorkOrders(WorkOrderSearchRequest request) {
        log.info("Searching work orders dynamically");
        Specification<WorkOrder> spec = WorkOrderSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<WorkOrder> page = workOrderRepository.findAll(spec, pageable);
        return page.map(workOrderMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteWorkOrder(Long id) {
        log.info("Soft deleting work order with ID: {}", id);
        WorkOrder entity = getWorkOrderOrThrow(id);
        entity.setActive(false);
        workOrderRepository.save(entity);
    }

    public void validateStatusTransition(WorkOrderStatus current, WorkOrderStatus next) {
        if (current == next) {
            return;
        }
        boolean valid = false;
        switch (current) {
            case REQUESTED:
                valid = next == WorkOrderStatus.ASSIGNED || next == WorkOrderStatus.CANCELLED;
                break;
            case ASSIGNED:
                valid = next == WorkOrderStatus.IN_PROGRESS || next == WorkOrderStatus.CANCELLED;
                break;
            case IN_PROGRESS:
                valid = next == WorkOrderStatus.COMPLETED || next == WorkOrderStatus.CANCELLED;
                break;
            case COMPLETED:
            case CANCELLED:
                valid = false; // Terminal states
                break;
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid work order status transition from " + current + " to " + next);
        }
    }

    private WorkOrder getWorkOrderOrThrow(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with ID: " + id));
    }
}
