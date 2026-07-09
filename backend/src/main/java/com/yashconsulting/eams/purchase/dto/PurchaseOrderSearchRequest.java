package com.yashconsulting.eams.purchase.dto;

import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching purchase orders dynamically with pagination and sorting")
public class PurchaseOrderSearchRequest {

    @Schema(description = "Filter by specific PO number (contains, case-insensitive)", example = "PO-2026")
    private String poNumber;

    @Schema(description = "Filter by vendor ID", example = "1")
    private Long vendorId;

    @Schema(description = "Filter by PO status state", example = "APPROVED")
    private PurchaseOrderStatus status;

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
