package com.yashconsulting.eams.workorder.controller;

import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderResponse;
import com.yashconsulting.eams.workorder.dto.WorkOrderSearchRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.service.WorkOrderService;
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
@RequestMapping("/api/v1/work-orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Work Order Management", description = "API endpoints for managing asset work orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new work order", description = "Allows administrators to schedule or register a new work order for an asset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Work order created successfully",
                    content = @Content(schema = @Schema(implementation = WorkOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Asset or Maintenance plan not found"),
            @ApiResponse(responseCode = "409", description = "Work order number already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<WorkOrderResponse> createWorkOrder(@Valid @RequestBody WorkOrderCreateRequest request) {
        WorkOrderResponse response = workOrderService.createWorkOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing work order", description = "Allows administrators to edit mutable fields of a work order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Work order updated successfully",
                    content = @Content(schema = @Schema(implementation = WorkOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or state transition"),
            @ApiResponse(responseCode = "404", description = "Work order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @Parameter(description = "ID of the work order to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderUpdateRequest request) {
        WorkOrderResponse response = workOrderService.updateWorkOrder(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Fetch a work order by ID", description = "Allows reading the configuration details of a specific work order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved work order details",
                    content = @Content(schema = @Schema(implementation = WorkOrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Work order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(
            @Parameter(description = "ID of the work order to fetch", example = "1")
            @PathVariable Long id) {
        WorkOrderResponse response = workOrderService.getWorkOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List work orders with pagination", description = "Retrieves a paginated list of work orders. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<WorkOrderResponse>> getAllWorkOrders(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive work orders in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<WorkOrderResponse> page = workOrderService.getAllWorkOrders(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search work orders dynamically", description = "Allows custom searching of work orders with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<WorkOrderResponse>> searchWorkOrders(@Valid WorkOrderSearchRequest request) {
        Page<WorkOrderResponse> page = workOrderService.searchWorkOrders(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a work order", description = "Removes a work order from active lists by soft deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Work order successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Work order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteWorkOrder(
            @Parameter(description = "ID of the work order to soft delete", example = "1")
            @PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return ResponseEntity.noContent().build();
    }
}
