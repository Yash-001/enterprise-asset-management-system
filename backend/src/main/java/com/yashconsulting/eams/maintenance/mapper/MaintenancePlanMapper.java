package com.yashconsulting.eams.maintenance.mapper;

import com.yashconsulting.eams.maintenance.dto.MaintenancePlanCreateRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanUpdateRequest;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import org.springframework.stereotype.Component;

@Component
public class MaintenancePlanMapper {

    public MaintenancePlan toEntity(MaintenancePlanCreateRequest request) {
        if (request == null) {
            return null;
        }

        return MaintenancePlan.builder()
                .assetId(request.getAssetId())
                .planCode(request.getPlanCode() != null ? request.getPlanCode().trim() : null)
                .planName(request.getPlanName() != null ? request.getPlanName().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .maintenanceType(request.getMaintenanceType())
                .frequencyType(request.getFrequencyType())
                .frequencyValue(request.getFrequencyValue())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .lastMaintenanceDate(request.getLastMaintenanceDate())
                .estimatedDurationHours(request.getEstimatedDurationHours())
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : MaintenanceStatus.SCHEDULED)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
    }

    public MaintenancePlanResponse toResponse(MaintenancePlan entity) {
        if (entity == null) {
            return null;
        }

        return MaintenancePlanResponse.builder()
                .id(entity.getId())
                .assetId(entity.getAssetId())
                .planCode(entity.getPlanCode())
                .planName(entity.getPlanName())
                .description(entity.getDescription())
                .maintenanceType(entity.getMaintenanceType())
                .frequencyType(entity.getFrequencyType())
                .frequencyValue(entity.getFrequencyValue())
                .nextMaintenanceDate(entity.getNextMaintenanceDate())
                .lastMaintenanceDate(entity.getLastMaintenanceDate())
                .estimatedDurationHours(entity.getEstimatedDurationHours())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(MaintenancePlanUpdateRequest request, MaintenancePlan entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setPlanName(request.getPlanName() != null ? request.getPlanName().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setMaintenanceType(request.getMaintenanceType());
        entity.setFrequencyType(request.getFrequencyType());
        entity.setFrequencyValue(request.getFrequencyValue());
        entity.setNextMaintenanceDate(request.getNextMaintenanceDate());
        entity.setLastMaintenanceDate(request.getLastMaintenanceDate());
        entity.setEstimatedDurationHours(request.getEstimatedDurationHours());
        entity.setPriority(request.getPriority());
        entity.setActive(request.getActive());
    }
}
