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
@Schema(description = "Request body payload to create a new stock transaction")
public class StockTransactionCreateRequest {

    @Schema(description = "ID of the associated spare part", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Spare part ID must not be null")
    private Long sparePartId;

    @Schema(description = "Transaction type (IN, OUT, ADJUSTMENT, RETURN)", example = "IN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Transaction type must not be null")
    private StockTransactionType transactionType;

    @Schema(description = "Quantity of items moved (can be signed for ADJUSTMENTS, e.g. -5 to reduce, +5 to increase)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity must not be null")
    private Integer quantity;

    @Schema(description = "Unit cost price per item at the time of transaction", example = "25.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Unit cost must not be null")
    @PositiveOrZero(message = "Unit cost must be positive or zero")
    private BigDecimal unitCost;

    @Schema(description = "Type of reference document or process (e.g. WORK_ORDER, PURCHASE_ORDER)", example = "WORK_ORDER")
    @Size(max = 100, message = "Reference type must not exceed 100 characters")
    private String referenceType;

    @Schema(description = "Optional ID referencing the associated reference document", example = "1")
    private Long referenceId;

    @Schema(description = "Additional transaction remarks or description", example = "Refilled stock for preventive maintenance")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
