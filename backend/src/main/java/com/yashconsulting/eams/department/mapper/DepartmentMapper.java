package com.yashconsulting.eams.department.mapper;

import com.yashconsulting.eams.department.dto.DepartmentCreateRequest;
import com.yashconsulting.eams.department.dto.DepartmentResponse;
import com.yashconsulting.eams.department.dto.DepartmentUpdateRequest;
import com.yashconsulting.eams.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Department.builder()
                .departmentCode(request.getDepartmentCode() != null ? request.getDepartmentCode().trim() : null)
                .departmentName(request.getDepartmentName() != null ? request.getDepartmentName().trim() : null)
                .manager(request.getManager() != null ? request.getManager().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
    }

    public DepartmentResponse toResponse(Department entity) {
        if (entity == null) {
            return null;
        }

        return DepartmentResponse.builder()
                .id(entity.getId())
                .departmentCode(entity.getDepartmentCode())
                .departmentName(entity.getDepartmentName())
                .manager(entity.getManager())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(DepartmentUpdateRequest request, Department entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setDepartmentName(request.getDepartmentName() != null ? request.getDepartmentName().trim() : null);
        entity.setManager(request.getManager() != null ? request.getManager().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setActive(request.getActive());
    }
}
