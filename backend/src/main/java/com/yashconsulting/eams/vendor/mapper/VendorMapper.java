package com.yashconsulting.eams.vendor.mapper;

import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.dto.VendorResponse;
import com.yashconsulting.eams.vendor.dto.VendorUpdateRequest;
import com.yashconsulting.eams.vendor.entity.Vendor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class VendorMapper {

    public Vendor toEntity(VendorCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Vendor.builder()
                .vendorCode(request.getVendorCode().trim().toUpperCase(Locale.ROOT))
                .vendorName(request.getVendorName().trim())
                .contactPerson(request.getContactPerson() != null ? request.getContactPerson().trim() : null)
                .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase(Locale.ROOT) : null)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .address(request.getAddress() != null ? request.getAddress().trim() : null)
                .city(request.getCity() != null ? request.getCity().trim() : null)
                .state(request.getState() != null ? request.getState().trim() : null)
                .country(request.getCountry() != null ? request.getCountry().trim() : null)
                .postalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null)
                .active(Boolean.TRUE)
                .build();
    }

    public VendorResponse toResponse(Vendor entity) {
        if (entity == null) {
            return null;
        }

        return VendorResponse.builder()
                .id(entity.getId())
                .vendorCode(entity.getVendorCode())
                .vendorName(entity.getVendorName())
                .contactPerson(entity.getContactPerson())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(VendorUpdateRequest request, Vendor entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setVendorName(request.getVendorName().trim());
        entity.setContactPerson(request.getContactPerson() != null ? request.getContactPerson().trim() : null);
        entity.setEmail(request.getEmail() != null ? request.getEmail().trim().toLowerCase(Locale.ROOT) : null);
        entity.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        entity.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        entity.setCity(request.getCity() != null ? request.getCity().trim() : null);
        entity.setState(request.getState() != null ? request.getState().trim() : null);
        entity.setCountry(request.getCountry() != null ? request.getCountry().trim() : null);
        entity.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);
        entity.setActive(request.getActive());
    }
}
