package com.yashconsulting.eams.workorder.service;

import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderResponse;
import com.yashconsulting.eams.workorder.dto.WorkOrderSearchRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderService {

    WorkOrderResponse createWorkOrder(WorkOrderCreateRequest request);

    WorkOrderResponse updateWorkOrder(Long id, WorkOrderUpdateRequest request);

    WorkOrderResponse getWorkOrderById(Long id);

    Page<WorkOrderResponse> getAllWorkOrders(Pageable pageable, boolean includeInactive);

    Page<WorkOrderResponse> searchWorkOrders(WorkOrderSearchRequest request);

    void deleteWorkOrder(Long id);
}
