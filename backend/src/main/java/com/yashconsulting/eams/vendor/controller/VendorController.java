package com.yashconsulting.eams.vendor.controller;

import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.dto.VendorResponse;
import com.yashconsulting.eams.vendor.dto.VendorSearchRequest;
import com.yashconsulting.eams.vendor.dto.VendorUpdateRequest;
import com.yashconsulting.eams.vendor.service.VendorService;
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
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vendor Management", description = "API endpoints for managing asset vendors")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new vendor", description = "Allows administrators to define and register a new vendor company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vendor successfully created",
                    content = @Content(schema = @Schema(implementation = VendorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Vendor code already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<VendorResponse> createVendor(@Valid @RequestBody VendorCreateRequest request) {
        VendorResponse response = vendorService.createVendor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing vendor", description = "Allows administrators to edit mutable fields of a vendor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor successfully updated",
                    content = @Content(schema = @Schema(implementation = VendorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<VendorResponse> updateVendor(
            @Parameter(description = "ID of the vendor to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody VendorUpdateRequest request) {
        VendorResponse response = vendorService.updateVendor(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Fetch a vendor by ID", description = "Allows reading the configuration details of a specific vendor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vendor details",
                    content = @Content(schema = @Schema(implementation = VendorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<VendorResponse> getVendorById(
            @Parameter(description = "ID of the vendor to fetch", example = "1")
            @PathVariable Long id) {
        VendorResponse response = vendorService.getVendorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List vendors with pagination", description = "Retrieves a paginated list of vendors. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<VendorResponse>> getAllVendors(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive vendors in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<VendorResponse> page = vendorService.getAllVendors(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search vendors dynamically", description = "Allows custom searching of vendors with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<VendorResponse>> searchVendors(@Valid VendorSearchRequest request) {
        Page<VendorResponse> page = vendorService.searchVendors(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a vendor", description = "Removes a vendor from active lists by soft deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vendor successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteVendor(
            @Parameter(description = "ID of the vendor to soft delete", example = "1")
            @PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
}
