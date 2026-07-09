package com.yashconsulting.eams.inventory.specification;

import com.yashconsulting.eams.inventory.dto.StockTransactionSearchRequest;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class StockTransactionSpecification {

    private StockTransactionSpecification() {
        // Private instantiation restriction
    }

    public static Specification<StockTransaction> hasSparePartId(Long sparePartId) {
        return (root, query, cb) -> {
            if (sparePartId == null) {
                return null;
            }
            return cb.equal(root.get("sparePartId"), sparePartId);
        };
    }

    public static Specification<StockTransaction> hasTransactionType(StockTransactionType transactionType) {
        return (root, query, cb) -> {
            if (transactionType == null) {
                return null;
            }
            return cb.equal(root.get("transactionType"), transactionType);
        };
    }

    public static Specification<StockTransaction> hasReferenceType(String referenceType) {
        return (root, query, cb) -> {
            if (referenceType == null || referenceType.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("referenceType")), "%" + referenceType.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<StockTransaction> hasPerformedBy(String performedBy) {
        return (root, query, cb) -> {
            if (performedBy == null || performedBy.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("performedBy")), "%" + performedBy.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<StockTransaction> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<StockTransaction> build(StockTransactionSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasSparePartId(request.getSparePartId()),
                hasTransactionType(request.getTransactionType()),
                hasReferenceType(request.getReferenceType()),
                hasPerformedBy(request.getPerformedBy()),
                isActive(activeFilter)
        );
    }
}
