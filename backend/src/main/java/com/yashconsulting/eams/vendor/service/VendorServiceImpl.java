package com.yashconsulting.eams.vendor.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.exception.VendorCodeAlreadyExistsException;
import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.dto.VendorResponse;
import com.yashconsulting.eams.vendor.dto.VendorSearchRequest;
import com.yashconsulting.eams.vendor.dto.VendorUpdateRequest;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.mapper.VendorMapper;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import com.yashconsulting.eams.vendor.specification.VendorSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;

    @Override
    @Transactional
    public VendorResponse createVendor(VendorCreateRequest request) {
        log.info("Creating new vendor with code {}", request.getVendorCode());

        String vendorCode = request.getVendorCode().trim().toUpperCase(Locale.ROOT);
        request.setVendorCode(vendorCode);

        if (vendorRepository.existsByVendorCode(vendorCode)) {
            throw new VendorCodeAlreadyExistsException("Vendor code " + vendorCode + " already exists");
        }

        Vendor entity = vendorMapper.toEntity(request);
        Vendor saved = vendorRepository.save(entity);
        return vendorMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public VendorResponse updateVendor(Long id, VendorUpdateRequest request) {
        log.info("Updating vendor with ID {}", id);
        Vendor entity = getVendorOrThrow(id);

        vendorMapper.updateEntity(request, entity);
        Vendor updated = vendorRepository.save(entity);
        return vendorMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long id) {
        log.info("Fetching vendor with ID {}", id);
        Vendor entity = getVendorOrThrow(id);
        return vendorMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable, boolean includeInactive) {
        log.info("Listing all vendors. Include inactive: {}", includeInactive);
        Page<Vendor> page = includeInactive
                ? vendorRepository.findAll(pageable)
                : vendorRepository.findAllByActiveTrue(pageable);
        return page.map(vendorMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> searchVendors(VendorSearchRequest request) {
        log.info("Searching vendors dynamically");
        Specification<Vendor> spec = VendorSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<Vendor> page = vendorRepository.findAll(spec, pageable);
        return page.map(vendorMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        log.info("Soft deleting vendor with ID: {}", id);
        Vendor entity = getVendorOrThrow(id);
        entity.setActive(false);
        vendorRepository.save(entity);
    }

    private Vendor getVendorOrThrow(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));
    }
}
