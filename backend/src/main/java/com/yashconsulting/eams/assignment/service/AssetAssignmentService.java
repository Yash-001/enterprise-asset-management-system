package com.yashconsulting.eams.assignment.service;

import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentSearchRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface AssetAssignmentService {

    AssetAssignmentResponse assignAsset(AssetAssignmentCreateRequest request);

    AssetAssignmentResponse updateAssignment(Long id, AssetAssignmentUpdateRequest request);

    AssetAssignmentResponse getAssignmentById(Long id);

    AssetAssignmentResponse returnAsset(Long id, LocalDate returnedDate, String remarks);

    Page<AssetAssignmentResponse> getAssignmentHistory(AssetAssignmentSearchRequest request);

    void deleteAssignment(Long id);
}
