package com.yashconsulting.eams.vendor.specification;

import com.yashconsulting.eams.vendor.dto.VendorSearchRequest;
import com.yashconsulting.eams.vendor.entity.Vendor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class VendorSpecification {

    private VendorSpecification() {
        // Private instantiation restriction
    }

    public static Specification<Vendor> hasVendorCode(String vendorCode) {
        return (root, query, cb) -> {
            if (vendorCode == null || vendorCode.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("vendorCode")), "%" + vendorCode.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Vendor> hasVendorName(String vendorName) {
        return (root, query, cb) -> {
            if (vendorName == null || vendorName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("vendorName")), "%" + vendorName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Vendor> hasContactPerson(String contactPerson) {
        return (root, query, cb) -> {
            if (contactPerson == null || contactPerson.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("contactPerson")), "%" + contactPerson.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Vendor> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Vendor> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("city")), "%" + city.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Vendor> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Vendor> build(VendorSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasVendorCode(request.getVendorCode()),
                hasVendorName(request.getVendorName()),
                hasContactPerson(request.getContactPerson()),
                hasEmail(request.getEmail()),
                hasCity(request.getCity()),
                isActive(activeFilter)
        );
    }
}
