package com.yashconsulting.eams.workorder.service;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.workorder.mapper.WorkOrderMapper;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceImplUnitTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private MaintenancePlanRepository maintenancePlanRepository;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    private WorkOrder testWorkOrder;

    @BeforeEach
    void setUp() {
        testWorkOrder = WorkOrder.builder()
                .id(1L)
                .workOrderNumber("WO-001")
                .assetId(10L)
                .title("Test Work Order")
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
    }

    @Test
    void validateStatusTransition_whenValidTransitions_thenSuccess() {
        // REQUESTED -> ASSIGNED / CANCELLED
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.REQUESTED, WorkOrderStatus.ASSIGNED));
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.REQUESTED, WorkOrderStatus.CANCELLED));

        // ASSIGNED -> IN_PROGRESS / CANCELLED
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.ASSIGNED, WorkOrderStatus.IN_PROGRESS));
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.ASSIGNED, WorkOrderStatus.CANCELLED));

        // IN_PROGRESS -> COMPLETED / CANCELLED
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED));
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED));

        // No transition (same status)
        assertDoesNotThrow(() -> workOrderService.validateStatusTransition(WorkOrderStatus.REQUESTED, WorkOrderStatus.REQUESTED));
    }

    @Test
    void validateStatusTransition_whenInvalidTransitions_thenThrowsException() {
        // REQUESTED -> IN_PROGRESS / COMPLETED (illegal)
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.REQUESTED, WorkOrderStatus.IN_PROGRESS));
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.REQUESTED, WorkOrderStatus.COMPLETED));

        // ASSIGNED -> REQUESTED / COMPLETED (illegal)
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.ASSIGNED, WorkOrderStatus.REQUESTED));
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.ASSIGNED, WorkOrderStatus.COMPLETED));

        // IN_PROGRESS -> REQUESTED / ASSIGNED (illegal)
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.REQUESTED));
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.ASSIGNED));

        // COMPLETED (Terminal) -> any
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.COMPLETED, WorkOrderStatus.REQUESTED));

        // CANCELLED (Terminal) -> any
        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.validateStatusTransition(WorkOrderStatus.CANCELLED, WorkOrderStatus.REQUESTED));
    }

    @Test
    void updateWorkOrder_whenInvalidTransition_thenThrowsException() {
        testWorkOrder.setStatus(WorkOrderStatus.COMPLETED); // Completed state
        WorkOrderUpdateRequest request = WorkOrderUpdateRequest.builder()
                .status(WorkOrderStatus.IN_PROGRESS) // Attempt to move back
                .build();

        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(testWorkOrder));

        assertThrows(IllegalArgumentException.class, () ->
                workOrderService.updateWorkOrder(1L, request));
    }
}
