package com.yashconsulting.eams.inventory.dto;

import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching stock transactions dynamically with pagination and sorting")
public class StockTransactionSearchRequest {

    @Schema(description = "Filter by specific spare part ID", example = "1")
    private Long sparePartId;

    @Schema(description = "Filter by specific transaction type (IN, OUT, ADJUSTMENT, RETURN)", example = "IN")
    private StockTransactionType transactionType;

    @Schema(description = "Filter by reference type (contains, case-insensitive)", example = "WORK")
    private String referenceType;

    @Schema(description = "Filter by performed by email (contains, case-insensitive)", example = "admin")
    private String performedBy;

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Schema(description = "Page index (0-indexed, default: 0)", example = "0")
    @Min(value = 0, message = "Page index must be at least 0")
    private Integer page = 0;

    @Schema(description = "Page size (default: 20, max: 100)", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 20;

    @Schema(description = "Field name to sort by", example = "id")
    private String sortBy = "id";

    @Schema(description = "Sort direction (ASC or DESC, default: ASC)", example = "ASC")
    private String sortDirection = "ASC";
}
