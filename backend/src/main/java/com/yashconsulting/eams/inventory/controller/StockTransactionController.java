package com.yashconsulting.eams.inventory.controller;

import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionResponse;
import com.yashconsulting.eams.inventory.dto.StockTransactionSearchRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionUpdateRequest;
import com.yashconsulting.eams.inventory.service.StockTransactionService;
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
@RequestMapping("/api/v1/stock-transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Stock Transaction Management", description = "API endpoints for managing spare part stock movements")
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Register a new stock transaction", description = "Allows administrators and managers to record inventory movements (IN, OUT, ADJUSTMENT, RETURN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock transaction successfully registered",
                    content = @Content(schema = @Schema(implementation = StockTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Spare part not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<StockTransactionResponse> createStockTransaction(@Valid @RequestBody StockTransactionCreateRequest request) {
        StockTransactionResponse response = stockTransactionService.createStockTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing stock transaction", description = "Allows administrators to edit stock transaction properties, which updates currentStock calculations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock transaction successfully updated",
                    content = @Content(schema = @Schema(implementation = StockTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or insufficient stock resulting from modification"),
            @ApiResponse(responseCode = "404", description = "Stock transaction not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<StockTransactionResponse> updateStockTransaction(
            @Parameter(description = "ID of the stock transaction to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StockTransactionUpdateRequest request) {
        StockTransactionResponse response = stockTransactionService.updateStockTransaction(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Fetch a stock transaction by ID", description = "Retrieves the detail logs of a specific stock transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction details",
                    content = @Content(schema = @Schema(implementation = StockTransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Stock transaction not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<StockTransactionResponse> getStockTransactionById(
            @Parameter(description = "ID of the stock transaction to fetch", example = "1")
            @PathVariable Long id) {
        StockTransactionResponse response = stockTransactionService.getStockTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "List stock transactions with pagination", description = "Retrieves a paginated list of stock transactions. Optionally includes inactive entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<StockTransactionResponse>> getAllStockTransactions(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @Parameter(description = "Flag to include inactive transactions in list", example = "false")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Page<StockTransactionResponse> page = stockTransactionService.getAllStockTransactions(pageable, includeInactive);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search stock transactions dynamically", description = "Allows custom searching of stock transactions with filter queries, pagination, and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully executed dynamic search"),
            @ApiResponse(responseCode = "400", description = "Invalid request query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<StockTransactionResponse>> searchStockTransactions(@Valid StockTransactionSearchRequest request) {
        Page<StockTransactionResponse> page = stockTransactionService.searchStockTransactions(request);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a stock transaction", description = "Removes a stock transaction and automatically reverts its stock modifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock transaction successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Stock transaction not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteStockTransaction(
            @Parameter(description = "ID of the stock transaction to soft delete", example = "1")
            @PathVariable Long id) {
        stockTransactionService.deleteStockTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
