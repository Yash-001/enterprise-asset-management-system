package com.yashconsulting.eams.asset.service;

import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetSearchRequest;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {

    AssetResponse createAsset(AssetCreateRequest request);

    AssetResponse updateAsset(Long id, AssetUpdateRequest request);

    AssetResponse getAssetById(Long id);

    default Page<AssetResponse> getAllAssets(Pageable pageable) {
        return getAllAssets(pageable, false);
    }

    Page<AssetResponse> getAllAssets(Pageable pageable, boolean includeInactive);

    Page<AssetResponse> searchAssets(AssetSearchRequest request);

    void deleteAsset(Long id);
}
