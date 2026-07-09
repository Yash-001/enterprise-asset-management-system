package com.yashconsulting.eams.asset.service;

import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetSearchRequest;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.mapper.AssetMapper;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.asset.specification.AssetSpecification;
import com.yashconsulting.eams.audit.annotation.Auditable;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.exception.AssetCodeAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
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
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    @Auditable(entity = "Asset", action = AuditAction.CREATE)
    public AssetResponse createAsset(AssetCreateRequest request) {
        String sanitizedAssetCode = request.getAssetCode().trim().toUpperCase(Locale.ROOT);

        if (assetRepository.existsByAssetCode(sanitizedAssetCode)) {
            throw new AssetCodeAlreadyExistsException("Asset code already exists: " + sanitizedAssetCode);
        }

        Asset asset = assetMapper.toEntity(request);
        asset.setAssetCode(sanitizedAssetCode);

        Asset savedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(savedAsset);
    }

    @Override
    @Transactional
    @Auditable(entity = "Asset", action = AuditAction.UPDATE)
    public AssetResponse updateAsset(Long id, AssetUpdateRequest request) {
        Asset asset = getAssetByIdOrThrow(id);

        if (request.getStatus() != null && request.getStatus() != asset.getStatus()) {
            if (!asset.getStatus().isValidTransition(request.getStatus())) {
                throw new IllegalArgumentException("Invalid asset status transition from " + asset.getStatus() + " to " + request.getStatus());
            }
        }

        assetMapper.updateEntity(request, asset);

        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(updatedAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(Long id) {
        Asset asset = getAssetByIdOrThrow(id);
        return assetMapper.toResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getAllAssets(Pageable pageable, boolean includeInactive) {
        Page<Asset> assetsPage = includeInactive
                ? assetRepository.findAll(pageable)
                : assetRepository.findAllByActiveTrue(pageable);
        return assetsPage.map(assetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> searchAssets(AssetSearchRequest request) {
        Specification<Asset> specification = AssetSpecification.build(request);

        Sort.Direction direction = Sort.Direction.ASC;
        if (request.getSortDirection() != null) {
            try {
                direction = Sort.Direction.fromString(request.getSortDirection().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                // Keep default ASC if input sorting is malformed
            }
        }

        String sortByField = (request.getSortBy() != null && !request.getSortBy().isBlank())
                ? request.getSortBy().trim()
                : "id";

        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortByField)
        );

        return assetRepository.findAll(specification, pageable)
                .map(assetMapper::toResponse);
    }

    @Override
    @Transactional
    @Auditable(entity = "Asset", action = AuditAction.DELETE)
    public void deleteAsset(Long id) {
        Asset asset = getAssetByIdOrThrow(id);

        if (Boolean.FALSE.equals(asset.getActive())) {
            return;
        }

        asset.setActive(false);
        assetRepository.save(asset);
    }

    private Asset getAssetByIdOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }
}
