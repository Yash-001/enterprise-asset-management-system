package com.yashconsulting.eams.inventory.dto;

import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing stock transaction details")
public class StockTransactionResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "ID of the associated spare part", example = "1")
    private Long sparePartId;

    @Schema(description = "Transaction type (IN, OUT, ADJUSTMENT, RETURN)", example = "IN")
    private StockTransactionType transactionType;

    @Schema(description = "Quantity of items moved", example = "10")
    private Integer quantity;

    @Schema(description = "Unit cost price per item", example = "25.00")
    private BigDecimal unitCost;

    @Schema(description = "Type of reference document or process", example = "WORK_ORDER")
    private String referenceType;

    @Schema(description = "Optional ID referencing the associated reference document", example = "1")
    private Long referenceId;

    @Schema(description = "Additional transaction remarks", example = "Refilled stock for preventive maintenance")
    private String remarks;

    @Schema(description = "Email or username of the user who performed the transaction", example = "admin@eams.com")
    private String performedBy;

    @Schema(description = "Timestamp when the transaction was executed", example = "2026-07-09T20:26:36")
    private LocalDateTime transactionDate;

    @Schema(description = "Flag indicating whether the stock transaction is active", example = "true")
    private Boolean active;
}
