package com.yashconsulting.eams.inventory.controller;

import com.yashconsulting.eams.inventory.dto.InventoryDashboardResponse;
import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartResponse;
import com.yashconsulting.eams.inventory.dto.SparePartSearchRequest;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import com.yashconsulting.eams.inventory.service.SparePartService;
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
@RequestMapping("/api/v1/spare-parts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Spare Parts Management", description = "API endpoints for managing spare parts inventory")
public class SparePartController {

    private final SparePartService sparePartService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new spare part", description = "Allows administrators to define and register a new spare part in the inventory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spare part created successfully",
                    content = @Content(schema = @Schema(implementation = SparePartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Storage location not found"),
            @ApiResponse(responseCode = "409", description = "Part number already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<SparePartResponse> createSparePart(@Valid @RequestBody SparePartCreateRequest request) {
        SparePartResponse response = sparePartService.createSparePart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing spare part", description = "Allows administrators to edit mutable fields of a spare part.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spare part updated successfully",
                    content = @Content(schema = @Schema(implementation = SparePartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Spare part or Location not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<SparePartResponse> updateSparePart(
            @Parameter(description = "ID of the spare part to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SparePartUpdateRequest request) {
        SparePartResponse response = sparePartService.updateSparePart(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Fetch a spare part by ID", description = "Allows reading the configuration details of a specific spare part.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved spare part details",
                    content = @Content(schema = @Schema(implementation = SparePartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Spare part not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<SparePartResponse> getSparePartById(
            @Parameter(description = "ID of the spare part to fetch", example = "1")
            @PathVariable Long id) {
        SparePartResponse response = sparePartService.getSparePartById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List spare parts with pagination", description = "Retrieves a paginated list of spare parts. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<SparePartResponse>> getAllSpareParts(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive spare parts in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<SparePartResponse> page = sparePartService.getAllSpareParts(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search spare parts dynamically", description = "Allows custom searching of spare parts with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<SparePartResponse>> searchSpareParts(@Valid SparePartSearchRequest request) {
        Page<SparePartResponse> page = sparePartService.searchSpareParts(request);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/dashboard/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get inventory dashboard aggregate metrics", description = "Retrieves active stock valuation and distribution groupings by category, supplier, and location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved aggregate metrics",
                    content = @Content(schema = @Schema(implementation = InventoryDashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<InventoryDashboardResponse> getInventoryDashboardMetrics() {
        InventoryDashboardResponse response = sparePartService.getInventoryDashboardMetrics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get low stock spare parts with pagination", description = "Retrieves a pageable list of active spare parts where currentStock <= minimumStock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated low stock items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<SparePartResponse>> getLowStockItems(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<SparePartResponse> page = sparePartService.getLowStockItems(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/dashboard/out-of-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get out of stock spare parts with pagination", description = "Retrieves a pageable list of active spare parts where currentStock = 0.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated out of stock items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<SparePartResponse>> getOutOfStockItems(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<SparePartResponse> page = sparePartService.getOutOfStockItems(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a spare part", description = "Removes a spare part from active lists by soft deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Spare part successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Spare part not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteSparePart(
            @Parameter(description = "ID of the spare part to soft delete", example = "1")
            @PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.noContent().build();
    }
}
