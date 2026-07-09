package com.yashconsulting.eams.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload representing a purchase order line item")
public class PurchaseOrderItemCreateRequest {

    @Schema(description = "ID of the associated spare part to order", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Spare part ID must not be null")
    private Long sparePartId;

    @Schema(description = "Quantity of items ordered", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Negotiated unit price per item at ordering time", example = "24.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Unit price must not be null")
    @PositiveOrZero(message = "Unit price must be positive or zero")
    private BigDecimal unitPrice;
}
