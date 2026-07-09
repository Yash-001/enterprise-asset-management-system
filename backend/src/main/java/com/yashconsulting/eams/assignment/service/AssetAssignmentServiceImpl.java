package com.yashconsulting.eams.assignment.service;

import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentSearchRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import com.yashconsulting.eams.assignment.entity.AssetAssignment;
import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import com.yashconsulting.eams.assignment.mapper.AssetAssignmentMapper;
import com.yashconsulting.eams.assignment.repository.AssetAssignmentRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetAssignmentServiceImpl implements AssetAssignmentService {

    private final AssetAssignmentRepository assetAssignmentRepository;
    private final AssetRepository assetRepository;
    private final AssetAssignmentMapper assetAssignmentMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AssetAssignmentResponse assignAsset(AssetAssignmentCreateRequest request) {
        log.info("Assigning asset {} to employee {}", request.getAssetId(), request.getEmployeeId());

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + request.getAssetId()));

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalArgumentException("Only AVAILABLE assets can be assigned. Current asset status: " + asset.getStatus());
        }

        if (assetAssignmentRepository.existsByAssetIdAndStatus(request.getAssetId(), AssignmentStatus.ACTIVE)) {
            throw new IllegalArgumentException("Asset is already actively assigned to an employee");
        }

        // Transition asset state
        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        AssetAssignment assignment = assetAssignmentMapper.toEntity(request);
        AssetAssignment saved = assetAssignmentRepository.save(assignment);
        
        eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.AssetAssignedEvent(
                saved.getAssetId(), asset.getAssetCode(), saved.getEmployeeId()));

        return assetAssignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AssetAssignmentResponse updateAssignment(Long id, AssetAssignmentUpdateRequest request) {
        log.info("Updating assignment ID: {}", id);
        AssetAssignment assignment = getAssignmentByIdOrThrow(id);

        assetAssignmentMapper.updateEntity(request, assignment);
        AssetAssignment updated = assetAssignmentRepository.save(assignment);
        return assetAssignmentMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetAssignmentResponse getAssignmentById(Long id) {
        log.info("Fetching assignment ID: {}", id);
        AssetAssignment assignment = getAssignmentByIdOrThrow(id);
        return assetAssignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional
    public AssetAssignmentResponse returnAsset(Long id, LocalDate returnedDate, String remarks) {
        log.info("Returning asset for assignment ID: {}", id);
        AssetAssignment assignment = getAssignmentByIdOrThrow(id);

        if (assignment.getStatus() == AssignmentStatus.RETURNED) {
            throw new IllegalArgumentException("Asset assignment is already returned");
        }

        Asset asset = assetRepository.findById(assignment.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assignment.getAssetId()));

        // Update state transitions
        assignment.setStatus(AssignmentStatus.RETURNED);
        assignment.setReturnedDate(returnedDate != null ? returnedDate : LocalDate.now());
        if (remarks != null && !remarks.isBlank()) {
            assignment.setRemarks(remarks.trim());
        }

        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        AssetAssignment saved = assetAssignmentRepository.save(assignment);
        return assetAssignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetAssignmentResponse> getAssignmentHistory(AssetAssignmentSearchRequest request) {
        log.info("Retrieving asset assignment history");

        Specification<AssetAssignment> spec = Specification.where(null);
        if (request.getAssetId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assetId"), request.getAssetId()));
        }
        if (request.getEmployeeId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("employeeId"), request.getEmployeeId()));
        }
        if (request.getStatus() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), request.getStatus()));
        }

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "DESC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<AssetAssignment> page = assetAssignmentRepository.findAll(spec, pageable);
        return page.map(assetAssignmentMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        log.info("Deleting assignment ID: {}", id);
        AssetAssignment assignment = getAssignmentByIdOrThrow(id);

        if (assignment.getStatus() == AssignmentStatus.ACTIVE) {
            Asset asset = assetRepository.findById(assignment.getAssetId()).orElse(null);
            if (asset != null) {
                asset.setStatus(AssetStatus.AVAILABLE);
                assetRepository.save(asset);
            }
        }

        assetAssignmentRepository.delete(assignment);
    }

    private AssetAssignment getAssignmentByIdOrThrow(Long id) {
        return assetAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset assignment not found with ID: " + id));
    }
}
