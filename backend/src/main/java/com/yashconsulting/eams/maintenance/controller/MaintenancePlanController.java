package com.yashconsulting.eams.maintenance.controller;

import com.yashconsulting.eams.maintenance.dto.MaintenanceDashboardResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanCreateRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanSearchRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanUpdateRequest;
import com.yashconsulting.eams.maintenance.service.MaintenancePlanService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenance-plans")
@RequiredArgsConstructor
@Validated
@Tag(name = "Preventive Maintenance Management", description = "Endpoints for managing preventive maintenance plans")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new maintenance plan", description = "Allows administrators to register a new preventive maintenance plan for an asset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Maintenance plan created successfully",
                    content = @Content(schema = @Schema(implementation = MaintenancePlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Plan code already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<MaintenancePlanResponse> createMaintenancePlan(@Valid @RequestBody MaintenancePlanCreateRequest request) {
        MaintenancePlanResponse response = maintenancePlanService.createMaintenancePlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing maintenance plan", description = "Allows administrators to edit mutable fields of a maintenance plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance plan updated successfully",
                    content = @Content(schema = @Schema(implementation = MaintenancePlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Maintenance plan not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<MaintenancePlanResponse> updateMaintenancePlan(
            @Parameter(description = "ID of the maintenance plan to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MaintenancePlanUpdateRequest request) {
        MaintenancePlanResponse response = maintenancePlanService.updateMaintenancePlan(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get maintenance dashboard metrics", description = "Retrieves optimized maintenance schedules aggregated by status and priority, as well as upcoming, overdue, and completed lists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard metrics successfully fetched",
                    content = @Content(schema = @Schema(implementation = MaintenanceDashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<MaintenanceDashboardResponse> getMaintenanceDashboard() {
        MaintenanceDashboardResponse response = maintenancePlanService.getMaintenanceDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get maintenance plan by ID", description = "Fetches a maintenance plan details by its primary key ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance plan found",
                    content = @Content(schema = @Schema(implementation = MaintenancePlanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Maintenance plan not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<MaintenancePlanResponse> getMaintenancePlanById(
            @Parameter(description = "ID of the maintenance plan to fetch", example = "1")
            @PathVariable Long id) {
        MaintenancePlanResponse response = maintenancePlanService.getMaintenancePlanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List maintenance plans with pagination", description = "Retrieves a paginated list of maintenance plans. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<MaintenancePlanResponse>> getAllMaintenancePlans(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive plans in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<MaintenancePlanResponse> page = maintenancePlanService.getAllMaintenancePlans(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search maintenance plans dynamically", description = "Allows custom searching of maintenance plans with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<MaintenancePlanResponse>> searchMaintenancePlans(@Valid MaintenancePlanSearchRequest request) {
        Page<MaintenancePlanResponse> page = maintenancePlanService.searchMaintenancePlans(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete maintenance plan", description = "Allows administrators to mark a maintenance plan as inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Maintenance plan soft deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Maintenance plan not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteMaintenancePlan(
            @Parameter(description = "ID of the maintenance plan to soft delete", example = "1")
            @PathVariable Long id) {
        maintenancePlanService.deleteMaintenancePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark maintenance plan as completed", description = "Calculates next scheduled date based on the completion date and frequency, updating the record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance plan completed and rescheduled successfully",
                    content = @Content(schema = @Schema(implementation = MaintenancePlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid completion date or details"),
            @ApiResponse(responseCode = "404", description = "Maintenance plan not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<MaintenancePlanResponse> completeMaintenancePlan(
            @Parameter(description = "ID of the maintenance plan to complete", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Actual completion date (defaults to today if omitted)", example = "2026-07-09")
            @RequestParam(required = false) java.time.LocalDate completionDate) {
        MaintenancePlanResponse response = maintenancePlanService.completeMaintenancePlan(id, completionDate);
        return ResponseEntity.ok(response);
    }
}
