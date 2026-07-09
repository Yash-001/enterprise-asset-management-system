package com.yashconsulting.eams.workorder.mapper;

import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderResponse;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderMapper {

    public WorkOrder toEntity(WorkOrderCreateRequest request) {
        if (request == null) {
            return null;
        }

        return WorkOrder.builder()
                .workOrderNumber(request.getWorkOrderNumber() != null ? request.getWorkOrderNumber().trim() : null)
                .assetId(request.getAssetId())
                .maintenancePlanId(request.getMaintenancePlanId())
                .title(request.getTitle() != null ? request.getTitle().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .assignedTechnician(request.getAssignedTechnician() != null ? request.getAssignedTechnician().trim() : null)
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : WorkOrderStatus.REQUESTED)
                .scheduledDate(request.getScheduledDate())
                .startDate(request.getStartDate())
                .completionDate(request.getCompletionDate())
                .estimatedHours(request.getEstimatedHours())
                .actualHours(request.getActualHours())
                .remarks(request.getRemarks() != null ? request.getRemarks().trim() : null)
                .active(Boolean.TRUE)
                .build();
    }

    public WorkOrderResponse toResponse(WorkOrder entity) {
        if (entity == null) {
            return null;
        }

        return WorkOrderResponse.builder()
                .id(entity.getId())
                .workOrderNumber(entity.getWorkOrderNumber())
                .assetId(entity.getAssetId())
                .maintenancePlanId(entity.getMaintenancePlanId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .assignedTechnician(entity.getAssignedTechnician())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .scheduledDate(entity.getScheduledDate())
                .startDate(entity.getStartDate())
                .completionDate(entity.getCompletionDate())
                .estimatedHours(entity.getEstimatedHours())
                .actualHours(entity.getActualHours())
                .remarks(entity.getRemarks())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(WorkOrderUpdateRequest request, WorkOrder entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setTitle(request.getTitle() != null ? request.getTitle().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setAssignedTechnician(request.getAssignedTechnician() != null ? request.getAssignedTechnician().trim() : null);
        entity.setPriority(request.getPriority());
        entity.setStatus(request.getStatus());
        entity.setScheduledDate(request.getScheduledDate());
        entity.setStartDate(request.getStartDate());
        entity.setCompletionDate(request.getCompletionDate());
        entity.setEstimatedHours(request.getEstimatedHours());
        entity.setActualHours(request.getActualHours());
        entity.setRemarks(request.getRemarks() != null ? request.getRemarks().trim() : null);
        entity.setActive(request.getActive());
    }
}
