package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.exception.SparePartNumberAlreadyExistsException;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartResponse;
import com.yashconsulting.eams.inventory.dto.SparePartSearchRequest;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.mapper.SparePartMapper;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.specification.SparePartSpecification;
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
public class SparePartServiceImpl implements SparePartService {

    private final SparePartRepository sparePartRepository;
    private final LocationRepository locationRepository;
    private final SparePartMapper sparePartMapper;

    @Override
    @Transactional
    public SparePartResponse createSparePart(SparePartCreateRequest request) {
        log.info("Creating new spare part: {}", request.getPartNumber());

        // Validate location exists if provided
        if (request.getLocationId() != null && !locationRepository.existsById(request.getLocationId())) {
            throw new ResourceNotFoundException("Location not found with ID: " + request.getLocationId());
        }

        // Validate part number uniqueness
        String partNumber = request.getPartNumber().trim().toUpperCase(Locale.ROOT);
        request.setPartNumber(partNumber);

        if (sparePartRepository.existsByPartNumber(partNumber)) {
            throw new SparePartNumberAlreadyExistsException("Spare part number " + partNumber + " already exists");
        }

        SparePart entity = sparePartMapper.toEntity(request);
        SparePart saved = sparePartRepository.save(entity);
        return sparePartMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SparePartResponse updateSparePart(Long id, SparePartUpdateRequest request) {
        log.info("Updating spare part with ID: {}", id);
        SparePart entity = getSparePartOrThrow(id);

        // Validate location exists if provided
        if (request.getLocationId() != null && !locationRepository.existsById(request.getLocationId())) {
            throw new ResourceNotFoundException("Location not found with ID: " + request.getLocationId());
        }

        sparePartMapper.updateEntity(request, entity);
        SparePart updated = sparePartRepository.save(entity);
        return sparePartMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SparePartResponse getSparePartById(Long id) {
        log.info("Fetching spare part with ID: {}", id);
        SparePart entity = getSparePartOrThrow(id);
        return sparePartMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SparePartResponse> getAllSpareParts(Pageable pageable, boolean includeInactive) {
        log.info("Listing all spare parts. Include inactive: {}", includeInactive);
        Page<SparePart> page = includeInactive
                ? sparePartRepository.findAll(pageable)
                : sparePartRepository.findAllByActiveTrue(pageable);
        return page.map(sparePartMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SparePartResponse> searchSpareParts(SparePartSearchRequest request) {
        log.info("Searching spare parts dynamically");
        Specification<SparePart> spec = SparePartSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<SparePart> page = sparePartRepository.findAll(spec, pageable);
        return page.map(sparePartMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteSparePart(Long id) {
        log.info("Soft deleting spare part with ID: {}", id);
        SparePart entity = getSparePartOrThrow(id);
        entity.setActive(false);
        sparePartRepository.save(entity);
    }

    private SparePart getSparePartOrThrow(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + id));
    }
}
