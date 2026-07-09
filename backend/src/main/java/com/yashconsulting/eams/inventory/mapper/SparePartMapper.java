package com.yashconsulting.eams.inventory.mapper;

import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartResponse;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SparePartMapper {

    public SparePart toEntity(SparePartCreateRequest request) {
        if (request == null) {
            return null;
        }

        return SparePart.builder()
                .partNumber(request.getPartNumber() != null ? request.getPartNumber().trim() : null)
                .partName(request.getPartName() != null ? request.getPartName().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .manufacturer(request.getManufacturer() != null ? request.getManufacturer().trim() : null)
                .category(request.getCategory() != null ? request.getCategory().trim() : null)
                .unitOfMeasure(request.getUnitOfMeasure() != null ? request.getUnitOfMeasure().trim() : null)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0)
                .maximumStock(request.getMaximumStock() != null ? request.getMaximumStock() : 0)
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : 0)
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : BigDecimal.ZERO)
                .supplierId(request.getSupplierId())
                .locationId(request.getLocationId())
                .active(Boolean.TRUE)
                .build();
    }

    public SparePartResponse toResponse(SparePart entity) {
        if (entity == null) {
            return null;
        }

        return SparePartResponse.builder()
                .id(entity.getId())
                .partNumber(entity.getPartNumber())
                .partName(entity.getPartName())
                .description(entity.getDescription())
                .manufacturer(entity.getManufacturer())
                .category(entity.getCategory())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .minimumStock(entity.getMinimumStock())
                .maximumStock(entity.getMaximumStock())
                .currentStock(entity.getCurrentStock())
                .unitCost(entity.getUnitCost())
                .supplierId(entity.getSupplierId())
                .locationId(entity.getLocationId())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(SparePartUpdateRequest request, SparePart entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setPartName(request.getPartName() != null ? request.getPartName().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setManufacturer(request.getManufacturer() != null ? request.getManufacturer().trim() : null);
        entity.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        entity.setUnitOfMeasure(request.getUnitOfMeasure() != null ? request.getUnitOfMeasure().trim() : null);
        entity.setMinimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0);
        entity.setMaximumStock(request.getMaximumStock() != null ? request.getMaximumStock() : 0);
        entity.setCurrentStock(request.getCurrentStock() != null ? request.getCurrentStock() : 0);
        entity.setUnitCost(request.getUnitCost() != null ? request.getUnitCost() : BigDecimal.ZERO);
        entity.setSupplierId(request.getSupplierId());
        entity.setLocationId(request.getLocationId());
        entity.setActive(request.getActive());
    }
}
