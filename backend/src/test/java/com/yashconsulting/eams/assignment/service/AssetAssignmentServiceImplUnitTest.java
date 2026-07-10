package com.yashconsulting.eams.assignment.service;

import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import com.yashconsulting.eams.assignment.entity.AssetAssignment;
import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import com.yashconsulting.eams.assignment.mapper.AssetAssignmentMapper;
import com.yashconsulting.eams.assignment.repository.AssetAssignmentRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetAssignmentServiceImplUnitTest {

    @Mock
    private AssetAssignmentRepository assetAssignmentRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetAssignmentMapper assetAssignmentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AssetAssignmentServiceImpl service;

    @Test
    void assignAsset_availableAsset_succeeds() {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(1L)
                .employeeId(10L)
                .build();

        Asset asset = Asset.builder().id(1L).assetCode("AST-001").status(AssetStatus.AVAILABLE).build();
        AssetAssignment assignment = AssetAssignment.builder().assetId(1L).employeeId(10L).build();
        AssetAssignment saved = AssetAssignment.builder().id(1L).assetId(1L).employeeId(10L).build();
        AssetAssignmentResponse response = AssetAssignmentResponse.builder().id(1L).build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetAssignmentRepository.existsByAssetIdAndStatus(1L, AssignmentStatus.ACTIVE)).thenReturn(false);
        when(assetAssignmentMapper.toEntity(request)).thenReturn(assignment);
        when(assetAssignmentRepository.save(assignment)).thenReturn(saved);
        when(assetAssignmentMapper.toResponse(saved)).thenReturn(response);

        AssetAssignmentResponse result = service.assignAsset(request);

        assertNotNull(result);
        assertEquals(AssetStatus.ASSIGNED, asset.getStatus());
        verify(assetRepository).save(asset);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void assignAsset_assetNotFound_throwsResourceNotFoundException() {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(99L)
                .employeeId(10L)
                .build();

        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.assignAsset(request));
        verify(assetAssignmentRepository, never()).save(any());
    }

    @Test
    void assignAsset_assetNotAvailable_throwsIllegalArgumentException() {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(1L)
                .employeeId(10L)
                .build();

        Asset asset = Asset.builder().id(1L).status(AssetStatus.ASSIGNED).build();
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assertThrows(IllegalArgumentException.class, () -> service.assignAsset(request));
        verify(assetAssignmentRepository, never()).save(any());
    }

    @Test
    void assignAsset_alreadyActiveAssignment_throwsIllegalArgumentException() {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(1L)
                .employeeId(10L)
                .build();

        Asset asset = Asset.builder().id(1L).status(AssetStatus.AVAILABLE).build();
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetAssignmentRepository.existsByAssetIdAndStatus(1L, AssignmentStatus.ACTIVE)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.assignAsset(request));
    }

    @Test
    void returnAsset_activeAssignment_transitionsToReturned() {
        AssetAssignment assignment = AssetAssignment.builder()
                .id(1L).assetId(1L).status(AssignmentStatus.ACTIVE).build();
        Asset asset = Asset.builder().id(1L).status(AssetStatus.ASSIGNED).build();
        AssetAssignmentResponse response = AssetAssignmentResponse.builder().id(1L).build();

        when(assetAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetAssignmentRepository.save(assignment)).thenReturn(assignment);
        when(assetAssignmentMapper.toResponse(assignment)).thenReturn(response);

        AssetAssignmentResponse result = service.returnAsset(1L, LocalDate.now(), "Returned in good condition");

        assertNotNull(result);
        assertEquals(AssignmentStatus.RETURNED, assignment.getStatus());
        assertEquals(AssetStatus.AVAILABLE, asset.getStatus());
        verify(assetRepository).save(asset);
    }

    @Test
    void returnAsset_alreadyReturned_throwsIllegalArgumentException() {
        AssetAssignment assignment = AssetAssignment.builder()
                .id(1L).assetId(1L).status(AssignmentStatus.RETURNED).build();

        when(assetAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        assertThrows(IllegalArgumentException.class, () -> service.returnAsset(1L, null, null));
    }

    @Test
    void getAssignmentById_existing_succeeds() {
        AssetAssignment assignment = AssetAssignment.builder().id(1L).build();
        AssetAssignmentResponse response = AssetAssignmentResponse.builder().id(1L).build();

        when(assetAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assetAssignmentMapper.toResponse(assignment)).thenReturn(response);

        AssetAssignmentResponse result = service.getAssignmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAssignmentById_nonExisting_throwsResourceNotFoundException() {
        when(assetAssignmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAssignmentById(99L));
    }

    @Test
    void deleteAssignment_activeAssignment_releasesAsset() {
        AssetAssignment assignment = AssetAssignment.builder()
                .id(1L).assetId(1L).status(AssignmentStatus.ACTIVE).build();
        Asset asset = Asset.builder().id(1L).status(AssetStatus.ASSIGNED).build();

        when(assetAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        service.deleteAssignment(1L);

        assertEquals(AssetStatus.AVAILABLE, asset.getStatus());
        verify(assetRepository).save(asset);
        verify(assetAssignmentRepository).delete(assignment);
    }
}
