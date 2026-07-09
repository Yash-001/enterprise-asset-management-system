package com.yashconsulting.eams.purchase.controller;

import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderResponse;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderSearchRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import com.yashconsulting.eams.purchase.service.PurchaseOrderService;
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
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Purchase Order Management", description = "API endpoints for managing parts purchasing flows")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new purchase order", description = "Allows administrators and managers to record a new purchase order in DRAFT status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase order created successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Vendor or Spare part not found"),
            @ApiResponse(responseCode = "409", description = "Purchase order number already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(@Valid @RequestBody PurchaseOrderCreateRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an existing purchase order", description = "Allows administrators and managers to modify expected delivery date, remarks, active status, items (only in DRAFT), or transition PO statuses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order successfully updated",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or illegal status transition"),
            @ApiResponse(responseCode = "404", description = "Purchase order or Spare part not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PurchaseOrderResponse> updatePurchaseOrder(
            @Parameter(description = "ID of the purchase order to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderUpdateRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Fetch a purchase order by ID", description = "Retrieves the detail logs of a specific purchase order including all line items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved purchase order details",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(
            @Parameter(description = "ID of the purchase order to fetch", example = "1")
            @PathVariable Long id) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List purchase orders with pagination", description = "Retrieves a paginated list of purchase orders. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<PurchaseOrderResponse>> getAllPurchaseOrders(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive purchase orders in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<PurchaseOrderResponse> page = purchaseOrderService.getAllPurchaseOrders(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search purchase orders dynamically", description = "Allows custom searching of purchase orders with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<PurchaseOrderResponse>> searchPurchaseOrders(@Valid PurchaseOrderSearchRequest request) {
        Page<PurchaseOrderResponse> page = purchaseOrderService.searchPurchaseOrders(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a purchase order", description = "Removes a purchase order from active lists by soft deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Purchase order successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deletePurchaseOrder(
            @Parameter(description = "ID of the purchase order to soft delete", example = "1")
            @PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
