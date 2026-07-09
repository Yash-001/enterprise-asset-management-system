package com.yashconsulting.eams.asset.service;

import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.mapper.AssetMapper;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.exception.AssetCodeAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplUnitTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    @Test
    void whenCreateAssetWithUniqueCode_thenSuccess() {
        AssetCreateRequest request = AssetCreateRequest.builder()
                .assetCode("ast-101")
                .assetName("MacBook Pro")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(2000.00))
                .status(AssetStatus.AVAILABLE)
                .build();

        Asset asset = Asset.builder()
                .assetCode("AST-101")
                .assetName("MacBook Pro")
                .build();

        Asset savedAsset = Asset.builder()
                .id(1L)
                .assetCode("AST-101")
                .assetName("MacBook Pro")
                .build();

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .assetCode("AST-101")
                .assetName("MacBook Pro")
                .build();

        when(assetRepository.existsByAssetCode("AST-101")).thenReturn(false);
        when(assetMapper.toEntity(request)).thenReturn(asset);
        when(assetRepository.save(asset)).thenReturn(savedAsset);
        when(assetMapper.toResponse(savedAsset)).thenReturn(response);

        AssetResponse result = assetService.createAsset(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("AST-101", result.getAssetCode());
        verify(assetRepository).existsByAssetCode("AST-101");
        verify(assetRepository).save(asset);
    }

    @Test
    void whenCreateAssetWithDuplicateCode_thenThrowsAssetCodeAlreadyExistsException() {
        AssetCreateRequest request = AssetCreateRequest.builder()
                .assetCode("ast-101")
                .build();

        when(assetRepository.existsByAssetCode("AST-101")).thenReturn(true);

        assertThrows(AssetCodeAlreadyExistsException.class, () -> assetService.createAsset(request));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void whenUpdateAssetWithValidTransition_thenSuccess() {
        Asset asset = Asset.builder()
                .id(1L)
                .assetCode("AST-101")
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        AssetUpdateRequest request = AssetUpdateRequest.builder()
                .status(AssetStatus.ASSIGNED)
                .build();

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .status(AssetStatus.ASSIGNED)
                .build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetRepository.save(asset)).thenReturn(asset);
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.updateAsset(1L, request);

        assertNotNull(result);
        assertEquals(AssetStatus.ASSIGNED, result.getStatus());
        verify(assetMapper).updateEntity(request, asset);
        verify(assetRepository).save(asset);
    }

    @Test
    void whenUpdateAssetWithInvalidTransition_thenThrowsIllegalArgumentException() {
        Asset asset = Asset.builder()
                .id(1L)
                .assetCode("AST-101")
                .status(AssetStatus.DISPOSED)
                .active(true)
                .build();

        AssetUpdateRequest request = AssetUpdateRequest.builder()
                .status(AssetStatus.AVAILABLE) // Disposed to Available is invalid
                .build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assertThrows(IllegalArgumentException.class, () -> assetService.updateAsset(1L, request));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void whenGetAssetByIdExisting_thenSuccess() {
        Asset asset = Asset.builder()
                .id(1L)
                .assetCode("AST-101")
                .build();

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .assetCode("AST-101")
                .build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetMapper.toResponse(asset)).thenReturn(response);

        AssetResponse result = assetService.getAssetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void whenGetAssetByIdNonExisting_thenThrowsResourceNotFoundException() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> assetService.getAssetById(99L));
    }

    @Test
    void whenDeleteAsset_thenSoftDeletes() {
        Asset asset = Asset.builder()
                .id(1L)
                .assetCode("AST-101")
                .active(true)
                .build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assetService.deleteAsset(1L);

        assertFalse(asset.getActive());
        verify(assetRepository).save(asset);
    }
}
