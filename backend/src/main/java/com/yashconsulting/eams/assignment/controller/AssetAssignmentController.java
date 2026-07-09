package com.yashconsulting.eams.assignment.controller;

import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentSearchRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import com.yashconsulting.eams.assignment.service.AssetAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/asset-assignments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Asset Assignment Management", description = "Endpoints for assigning assets to employees and tracking return states")
public class AssetAssignmentController {

    private final AssetAssignmentService assetAssignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Assign an asset to an employee", description = "Allows admins and managers to allocate an AVAILABLE asset to an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asset assigned successfully",
                    content = @Content(schema = @Schema(implementation = AssetAssignmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AssetAssignmentResponse> assignAsset(@Valid @RequestBody AssetAssignmentCreateRequest request) {
        AssetAssignmentResponse response = assetAssignmentService.assignAsset(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update assignment details", description = "Allows editing assignment metadata such as expected return date and remarks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment updated successfully",
                    content = @Content(schema = @Schema(implementation = AssetAssignmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AssetAssignmentResponse> updateAssignment(
            @Parameter(description = "ID of the assignment record to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AssetAssignmentUpdateRequest request) {
        AssetAssignmentResponse response = assetAssignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get assignment details by ID", description = "Fetches details of a specific assignment record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment details found",
                    content = @Content(schema = @Schema(implementation = AssetAssignmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AssetAssignmentResponse> getAssignmentById(
            @Parameter(description = "ID of the assignment to retrieve", example = "1")
            @PathVariable Long id) {
        AssetAssignmentResponse response = assetAssignmentService.getAssignmentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark assigned asset as returned", description = "Records actual return date, updates assignment status, and reverts asset status back to AVAILABLE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset returned successfully",
                    content = @Content(schema = @Schema(implementation = AssetAssignmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Business rule violation (e.g. already returned)"),
            @ApiResponse(responseCode = "404", description = "Assignment or Asset not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AssetAssignmentResponse> returnAsset(
            @Parameter(description = "ID of the assignment record being returned", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Actual return date (defaults to today if omitted)", example = "2026-07-09")
            @RequestParam(required = false) LocalDate returnedDate,
            @Parameter(description = "Optional additional remarks regarding the return condition", example = "Returned in pristine condition")
            @RequestParam(required = false) String remarks) {
        AssetAssignmentResponse response = assetAssignmentService.returnAsset(id, returnedDate, remarks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get assignment history", description = "Lists paginated assignment records, with optional filters by asset, employee, or status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved assignment history"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<AssetAssignmentResponse>> getAssignmentHistory(@Valid AssetAssignmentSearchRequest request) {
        Page<AssetAssignmentResponse> history = assetAssignmentService.getAssignmentHistory(request);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an assignment record", description = "Removes assignment record and resets asset status back to AVAILABLE if assignment was active.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assignment record deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteAssignment(
            @Parameter(description = "ID of the assignment record to delete", example = "1")
            @PathVariable Long id) {
        assetAssignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
