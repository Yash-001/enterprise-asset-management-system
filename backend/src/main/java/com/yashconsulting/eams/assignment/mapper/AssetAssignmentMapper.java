package com.yashconsulting.eams.assignment.mapper;

import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import com.yashconsulting.eams.assignment.entity.AssetAssignment;
import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AssetAssignmentMapper {

    public AssetAssignment toEntity(AssetAssignmentCreateRequest request) {
        if (request == null) {
            return null;
        }

        return AssetAssignment.builder()
                .assetId(request.getAssetId())
                .employeeId(request.getEmployeeId())
                .assignedDate(request.getAssignedDate())
                .expectedReturnDate(request.getExpectedReturnDate())
                .remarks(request.getRemarks() != null ? request.getRemarks().trim() : null)
                .status(AssignmentStatus.ACTIVE)
                .build();
    }

    public AssetAssignmentResponse toResponse(AssetAssignment entity) {
        if (entity == null) {
            return null;
        }

        return AssetAssignmentResponse.builder()
                .id(entity.getId())
                .assetId(entity.getAssetId())
                .employeeId(entity.getEmployeeId())
                .assignedDate(entity.getAssignedDate())
                .expectedReturnDate(entity.getExpectedReturnDate())
                .returnedDate(entity.getReturnedDate())
                .remarks(entity.getRemarks())
                .status(entity.getStatus())
                .build();
    }

    public void updateEntity(AssetAssignmentUpdateRequest request, AssetAssignment entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setExpectedReturnDate(request.getExpectedReturnDate());
        entity.setRemarks(request.getRemarks() != null ? request.getRemarks().trim() : null);
    }
}
