package com.yashconsulting.eams.purchase.dto;

import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing purchase order details")
public class PurchaseOrderResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique alphanumeric PO number code", example = "PO-2026-001")
    private String poNumber;

    @Schema(description = "ID of associated vendor", example = "1")
    private Long vendorId;

    @Schema(description = "Current status workflow state", example = "APPROVED")
    private PurchaseOrderStatus status;

    @Schema(description = "Date and time when the PO was recorded", example = "2026-07-09T20:33:43")
    private LocalDateTime orderDate;

    @Schema(description = "Planned date of when order delivery is expected", example = "2026-07-20T12:00:00")
    private LocalDateTime expectedDeliveryDate;

    @Schema(description = "Sum total cost of all PO line items", example = "420.00")
    private BigDecimal totalAmount;

    @Schema(description = "Notes or instructions", example = "Rush order delivery requested")
    private String remarks;

    @Schema(description = "Flag indicating whether the purchase order is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the PO record was created", example = "2026-07-09T20:33:43")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the PO record was last updated", example = "2026-07-09T20:33:43")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;

    @Schema(description = "List of line items included in the purchase order")
    private List<PurchaseOrderItemResponse> items;
}
