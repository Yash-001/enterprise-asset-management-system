package com.yashconsulting.eams.vendor.service;

import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.dto.VendorResponse;
import com.yashconsulting.eams.vendor.dto.VendorSearchRequest;
import com.yashconsulting.eams.vendor.dto.VendorUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorService {

    VendorResponse createVendor(VendorCreateRequest request);

    VendorResponse updateVendor(Long id, VendorUpdateRequest request);

    VendorResponse getVendorById(Long id);

    Page<VendorResponse> getAllVendors(Pageable pageable, boolean includeInactive);

    Page<VendorResponse> searchVendors(VendorSearchRequest request);

    void deleteVendor(Long id);
}
