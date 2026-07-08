package com.yashconsulting.eams.asset.mapper;

import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import com.yashconsulting.eams.asset.entity.Asset;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AssetMapper {

    public Asset toEntity(AssetCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        return Asset.builder()
                .assetCode(dto.getAssetCode() != null ? dto.getAssetCode().trim().toUpperCase(Locale.ROOT) : null)
                .assetName(dto.getAssetName() != null ? dto.getAssetName().trim() : null)
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .serialNumber(dto.getSerialNumber() != null ? dto.getSerialNumber().trim() : null)
                .manufacturer(dto.getManufacturer() != null ? dto.getManufacturer().trim() : null)
                .model(dto.getModel() != null ? dto.getModel().trim() : null)
                .purchaseDate(dto.getPurchaseDate())
                .purchasePrice(dto.getPurchasePrice())
                .warrantyExpiry(dto.getWarrantyExpiry())
                .status(dto.getStatus())
                .departmentId(dto.getDepartmentId())
                .locationId(dto.getLocationId())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public AssetResponse toResponse(Asset entity) {
        if (entity == null) {
            return null;
        }

        return AssetResponse.builder()
                .id(entity.getId())
                .assetCode(entity.getAssetCode())
                .assetName(entity.getAssetName())
                .description(entity.getDescription())
                .serialNumber(entity.getSerialNumber())
                .manufacturer(entity.getManufacturer())
                .model(entity.getModel())
                .purchaseDate(entity.getPurchaseDate())
                .purchasePrice(entity.getPurchasePrice())
                .warrantyExpiry(entity.getWarrantyExpiry())
                .status(entity.getStatus())
                .departmentId(entity.getDepartmentId())
                .locationId(entity.getLocationId())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(AssetUpdateRequest dto, Asset entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getAssetName() != null && !dto.getAssetName().isBlank()) {
            entity.setAssetName(dto.getAssetName().trim());
        }

        entity.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        entity.setSerialNumber(dto.getSerialNumber() != null ? dto.getSerialNumber().trim() : null);
        entity.setManufacturer(dto.getManufacturer() != null ? dto.getManufacturer().trim() : null);
        entity.setModel(dto.getModel() != null ? dto.getModel().trim() : null);

        if (dto.getPurchaseDate() != null) {
            entity.setPurchaseDate(dto.getPurchaseDate());
        }

        if (dto.getPurchasePrice() != null) {
            entity.setPurchasePrice(dto.getPurchasePrice());
        }

        entity.setWarrantyExpiry(dto.getWarrantyExpiry());

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        entity.setDepartmentId(dto.getDepartmentId());
        entity.setLocationId(dto.getLocationId());

        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
