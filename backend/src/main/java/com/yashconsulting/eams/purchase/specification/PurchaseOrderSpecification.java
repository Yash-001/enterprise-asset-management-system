package com.yashconsulting.eams.purchase.specification;

import com.yashconsulting.eams.purchase.dto.PurchaseOrderSearchRequest;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class PurchaseOrderSpecification {

    private PurchaseOrderSpecification() {
        // Private instantiation restriction
    }

    public static Specification<PurchaseOrder> hasPoNumber(String poNumber) {
        return (root, query, cb) -> {
            if (poNumber == null || poNumber.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("poNumber")), "%" + poNumber.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<PurchaseOrder> hasVendorId(Long vendorId) {
        return (root, query, cb) -> {
            if (vendorId == null) {
                return null;
            }
            return cb.equal(root.get("vendorId"), vendorId);
        };
    }

    public static Specification<PurchaseOrder> hasStatus(PurchaseOrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<PurchaseOrder> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<PurchaseOrder> build(PurchaseOrderSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasPoNumber(request.getPoNumber()),
                hasVendorId(request.getVendorId()),
                hasStatus(request.getStatus()),
                isActive(activeFilter)
        );
    }
}
