package com.yashconsulting.eams.maintenance.specification;

import com.yashconsulting.eams.maintenance.dto.MaintenancePlanSearchRequest;
import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class MaintenancePlanSpecification {

    private MaintenancePlanSpecification() {
        // Private instantiation restriction
    }

    public static Specification<MaintenancePlan> hasAssetId(Long assetId) {
        return (root, query, cb) -> {
            if (assetId == null) {
                return null;
            }
            return cb.equal(root.get("assetId"), assetId);
        };
    }

    public static Specification<MaintenancePlan> hasPlanCode(String planCode) {
        return (root, query, cb) -> {
            if (planCode == null || planCode.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("planCode")), "%" + planCode.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<MaintenancePlan> hasPlanName(String planName) {
        return (root, query, cb) -> {
            if (planName == null || planName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("planName")), "%" + planName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<MaintenancePlan> hasMaintenanceType(MaintenanceType maintenanceType) {
        return (root, query, cb) -> {
            if (maintenanceType == null) {
                return null;
            }
            return cb.equal(root.get("maintenanceType"), maintenanceType);
        };
    }

    public static Specification<MaintenancePlan> hasPriority(MaintenancePriority priority) {
        return (root, query, cb) -> {
            if (priority == null) {
                return null;
            }
            return cb.equal(root.get("priority"), priority);
        };
    }

    public static Specification<MaintenancePlan> hasStatus(MaintenanceStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<MaintenancePlan> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<MaintenancePlan> build(MaintenancePlanSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasAssetId(request.getAssetId()),
                hasPlanCode(request.getPlanCode()),
                hasPlanName(request.getPlanName()),
                hasMaintenanceType(request.getMaintenanceType()),
                hasPriority(request.getPriority()),
                hasStatus(request.getStatus()),
                isActive(activeFilter)
        );
    }
}
