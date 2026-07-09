package com.yashconsulting.eams.location.controller;

import com.yashconsulting.eams.location.dto.LocationCreateRequest;
import com.yashconsulting.eams.location.dto.LocationResponse;
import com.yashconsulting.eams.location.dto.LocationSearchRequest;
import com.yashconsulting.eams.location.dto.LocationUpdateRequest;
import com.yashconsulting.eams.location.service.LocationService;
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
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Location Management", description = "Endpoints for managing corporate locations")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new location", description = "Allows administrators to register a new location with a unique code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created successfully",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Location code already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationCreateRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing location", description = "Allows administrators to edit the mutable fields of a location profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<LocationResponse> updateLocation(
            @Parameter(description = "ID of the location to be updated", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LocationUpdateRequest request) {
        LocationResponse response = locationService.updateLocation(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get location by ID", description = "Fetches a location profile by its primary key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<LocationResponse> getLocationById(
            @Parameter(description = "ID of the location to fetch", example = "1")
            @PathVariable Long id) {
        LocationResponse response = locationService.getLocationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List locations with pagination", description = "Retrieves a paginated list of locations. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<LocationResponse>> getAllLocations(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive locations in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<LocationResponse> page = locationService.getAllLocations(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search locations dynamically", description = "Allows custom searching of locations with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<LocationResponse>> searchLocations(@Valid LocationSearchRequest request) {
        Page<LocationResponse> page = locationService.searchLocations(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete location", description = "Allows administrators to mark a location profile as inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location soft deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteLocation(
            @Parameter(description = "ID of the location to soft delete", example = "1")
            @PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
