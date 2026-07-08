package com.yashconsulting.eams.user.controller;

import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserSearchRequest;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.exception.ErrorResponse;
import com.yashconsulting.eams.user.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users (CRUD & search)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Registers a new user inside the system. Email address must be unique. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation constraint violations", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email address is already in use", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the new user to register", required = true)
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get user by ID", description = "Retrieves a user profile by their database ID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "42") @PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all users (paginated)", description = "Retrieves a paginated list of all registered users. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users list retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content)
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Flag to include deactivated users in results", example = "false")
            @RequestParam(value = "includeInactive", required = false, defaultValue = "false") boolean includeInactive,
            Pageable pageable) {
        Page<UserResponse> response = userService.getAllUsers(pageable, includeInactive);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search users dynamically", description = "Performs dynamic case-insensitive search based on filter inputs (firstName, lastName, email, active). Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search query completed successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search/pagination parameters", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN or MANAGER role)", content = @Content)
    })
    public ResponseEntity<Page<UserResponse>> searchUsers(@Valid UserSearchRequest request) {
        Page<UserResponse> response = userService.searchUsers(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user details", description = "Updates the user's first name, last name, or active status. Email and password are not updatable via this API. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation constraint violations", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID of the user to update", required = true, example = "42") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user fields", required = true)
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a user", description = "Deactivates the user by setting the active flag to false. Historical records are preserved. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User soft deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access (requires ADMIN role)", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to soft delete", required = true, example = "42") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
