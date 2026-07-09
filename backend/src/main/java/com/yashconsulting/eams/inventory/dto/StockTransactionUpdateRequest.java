package com.yashconsulting.eams.inventory.dto;

import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update an existing stock transaction")
public class StockTransactionUpdateRequest {

    @Schema(description = "Transaction type (IN, OUT, ADJUSTMENT, RETURN)", example = "IN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Transaction type must not be null")
    private StockTransactionType transactionType;

    @Schema(description = "Quantity of items moved (can be signed for ADJUSTMENTS)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity must not be null")
    private Integer quantity;

    @Schema(description = "Unit cost price per item", example = "25.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Unit cost must not be null")
    @PositiveOrZero(message = "Unit cost must be positive or zero")
    private BigDecimal unitCost;

    @Schema(description = "Type of reference document or process", example = "WORK_ORDER")
    @Size(max = 100, message = "Reference type must not exceed 100 characters")
    private String referenceType;

    @Schema(description = "Optional ID referencing the associated reference document", example = "1")
    private Long referenceId;

    @Schema(description = "Additional transaction remarks", example = "Correction of manual entry")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Schema(description = "Flag indicating whether the stock transaction is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
