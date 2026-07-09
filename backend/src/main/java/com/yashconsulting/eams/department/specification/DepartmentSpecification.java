package com.yashconsulting.eams.department.specification;

import com.yashconsulting.eams.department.dto.DepartmentSearchRequest;
import com.yashconsulting.eams.department.entity.Department;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class DepartmentSpecification {

    private DepartmentSpecification() {
        // Utility class constructor private instantiation restriction
    }

    public static Specification<Department> hasDepartmentCode(String departmentCode) {
        return (root, query, cb) -> {
            if (departmentCode == null || departmentCode.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("departmentCode")), "%" + departmentCode.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Department> hasDepartmentName(String departmentName) {
        return (root, query, cb) -> {
            if (departmentName == null || departmentName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("departmentName")), "%" + departmentName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Department> hasManager(String manager) {
        return (root, query, cb) -> {
            if (manager == null || manager.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("manager")), "%" + manager.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Department> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Department> build(DepartmentSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasDepartmentCode(request.getDepartmentCode()),
                hasDepartmentName(request.getDepartmentName()),
                hasManager(request.getManager()),
                isActive(activeFilter)
        );
    }
}
