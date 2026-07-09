package com.yashconsulting.eams.workorder.specification;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.workorder.dto.WorkOrderSearchRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class WorkOrderSpecification {

    private WorkOrderSpecification() {
        // Private instantiation restriction
    }

    public static Specification<WorkOrder> hasWorkOrderNumber(String workOrderNumber) {
        return (root, query, cb) -> {
            if (workOrderNumber == null || workOrderNumber.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("workOrderNumber")), "%" + workOrderNumber.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<WorkOrder> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<WorkOrder> hasAssetId(Long assetId) {
        return (root, query, cb) -> {
            if (assetId == null) {
                return null;
            }
            return cb.equal(root.get("assetId"), assetId);
        };
    }

    public static Specification<WorkOrder> hasMaintenancePlanId(Long maintenancePlanId) {
        return (root, query, cb) -> {
            if (maintenancePlanId == null) {
                return null;
            }
            return cb.equal(root.get("maintenancePlanId"), maintenancePlanId);
        };
    }

    public static Specification<WorkOrder> hasAssignedTechnician(String assignedTechnician) {
        return (root, query, cb) -> {
            if (assignedTechnician == null || assignedTechnician.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("assignedTechnician")), "%" + assignedTechnician.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<WorkOrder> hasPriority(MaintenancePriority priority) {
        return (root, query, cb) -> {
            if (priority == null) {
                return null;
            }
            return cb.equal(root.get("priority"), priority);
        };
    }

    public static Specification<WorkOrder> hasStatus(WorkOrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<WorkOrder> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<WorkOrder> build(WorkOrderSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasWorkOrderNumber(request.getWorkOrderNumber()),
                hasTitle(request.getTitle()),
                hasAssetId(request.getAssetId()),
                hasMaintenancePlanId(request.getMaintenancePlanId()),
                hasAssignedTechnician(request.getAssignedTechnician()),
                hasPriority(request.getPriority()),
                hasStatus(request.getStatus()),
                isActive(activeFilter)
        );
    }
}
