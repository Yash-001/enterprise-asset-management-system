package com.yashconsulting.eams.inventory.specification;

import com.yashconsulting.eams.inventory.dto.SparePartSearchRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class SparePartSpecification {

    private SparePartSpecification() {
        // Private instantiation restriction
    }

    public static Specification<SparePart> hasPartNumber(String partNumber) {
        return (root, query, cb) -> {
            if (partNumber == null || partNumber.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("partNumber")), "%" + partNumber.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<SparePart> hasPartName(String partName) {
        return (root, query, cb) -> {
            if (partName == null || partName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("partName")), "%" + partName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<SparePart> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("category")), "%" + category.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<SparePart> hasManufacturer(String manufacturer) {
        return (root, query, cb) -> {
            if (manufacturer == null || manufacturer.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("manufacturer")), "%" + manufacturer.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<SparePart> hasLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) {
                return null;
            }
            return cb.equal(root.get("locationId"), locationId);
        };
    }

    public static Specification<SparePart> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<SparePart> build(SparePartSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasPartNumber(request.getPartNumber()),
                hasPartName(request.getPartName()),
                hasCategory(request.getCategory()),
                hasManufacturer(request.getManufacturer()),
                hasLocationId(request.getLocationId()),
                isActive(activeFilter)
        );
    }
}
