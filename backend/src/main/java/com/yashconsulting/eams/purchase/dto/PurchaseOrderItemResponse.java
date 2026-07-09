package com.yashconsulting.eams.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing details of a purchase order item line")
public class PurchaseOrderItemResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "ID of parent purchase order", example = "1")
    private Long purchaseOrderId;

    @Schema(description = "ID of associated spare part in inventory", example = "1")
    private Long sparePartId;

    @Schema(description = "Quantity ordered", example = "5")
    private Integer quantity;

    @Schema(description = "Price per item unit", example = "24.50")
    private BigDecimal unitPrice;

    @Schema(description = "Total cost value (quantity * unitPrice) for this line", example = "122.50")
    private BigDecimal lineTotal;
}
