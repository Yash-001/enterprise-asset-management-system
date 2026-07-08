package com.yashconsulting.eams.asset.specification;

import com.yashconsulting.eams.asset.dto.AssetSearchRequest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class AssetSpecification {

    private AssetSpecification() {
        // Utility class constructor private instantiation restriction
    }

    public static Specification<Asset> hasAssetCode(String assetCode) {
        return (root, query, cb) -> {
            if (assetCode == null || assetCode.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("assetCode")), "%" + assetCode.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Asset> hasAssetName(String assetName) {
        return (root, query, cb) -> {
            if (assetName == null || assetName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("assetName")), "%" + assetName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Asset> hasSerialNumber(String serialNumber) {
        return (root, query, cb) -> {
            if (serialNumber == null || serialNumber.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("serialNumber")), "%" + serialNumber.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Asset> hasManufacturer(String manufacturer) {
        return (root, query, cb) -> {
            if (manufacturer == null || manufacturer.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("manufacturer")), "%" + manufacturer.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Asset> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("model")), "%" + model.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Asset> hasStatus(AssetStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Asset> hasDepartmentId(Long departmentId) {
        return (root, query, cb) -> {
            if (departmentId == null) {
                return null;
            }
            return cb.equal(root.get("departmentId"), departmentId);
        };
    }

    public static Specification<Asset> hasLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) {
                return null;
            }
            return cb.equal(root.get("locationId"), locationId);
        };
    }

    public static Specification<Asset> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Asset> build(AssetSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasAssetCode(request.getAssetCode()),
                hasAssetName(request.getAssetName()),
                hasSerialNumber(request.getSerialNumber()),
                hasManufacturer(request.getManufacturer()),
                hasModel(request.getModel()),
                hasStatus(request.getStatus()),
                hasDepartmentId(request.getDepartmentId()),
                hasLocationId(request.getLocationId()),
                isActive(activeFilter)
        );
    }
}
