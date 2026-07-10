package com.yashconsulting.eams.asset.controller;

import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetSearchRequest;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import com.yashconsulting.eams.exception.ErrorResponse;
import com.yashconsulting.eams.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "APIs for managing assets (CRUD & search)")
@SecurityRequirement(name = "Bearer JWT")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new asset", description = "Registers a new asset inside the system. Asset code must be unique. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asset created successfully",
                    content = @Content(schema = @Schema(implementation = AssetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation constraint violations", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Asset code is already in use", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AssetResponse> createAsset(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the new asset to register", required = true)
            @Valid @RequestBody AssetCreateRequest request) {
        AssetResponse response = assetService.createAsset(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get asset by ID", description = "Retrieves asset details by its database ID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AssetResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Asset not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AssetResponse> getAssetById(
            @Parameter(description = "ID of the asset to retrieve", required = true, example = "1") @PathVariable Long id) {
        AssetResponse response = assetService.getAssetById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all assets (paginated)", description = "Retrieves a paginated list of all registered assets. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assets list retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content)
    })
    public ResponseEntity<Page<AssetResponse>> getAllAssets(
            @Parameter(description = "Flag to include deactivated assets in results", example = "false")
            @RequestParam(value = "includeInactive", required = false, defaultValue = "false") boolean includeInactive,
            Pageable pageable) {
        Page<AssetResponse> response = assetService.getAllAssets(pageable, includeInactive);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search assets dynamically", description = "Performs dynamic case-insensitive search based on filter inputs. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search query completed successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search/pagination parameters", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content)
    })
    public ResponseEntity<Page<AssetResponse>> searchAssets(@Valid AssetSearchRequest request) {
        Page<AssetResponse> response = assetService.searchAssets(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update asset details", description = "Updates the asset fields. Asset code is not updatable via this API. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset details updated successfully",
                    content = @Content(schema = @Schema(implementation = AssetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation constraint violations", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Asset not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AssetResponse> updateAsset(
            @Parameter(description = "ID of the asset to update", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated asset fields", required = true)
            @Valid @RequestBody AssetUpdateRequest request) {
        AssetResponse response = assetService.updateAsset(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete an asset", description = "Deactivates the asset by setting the active flag to false. Historical records are preserved. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asset soft deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Asset not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAsset(
            @Parameter(description = "ID of the asset to soft delete", required = true, example = "1") @PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
}
