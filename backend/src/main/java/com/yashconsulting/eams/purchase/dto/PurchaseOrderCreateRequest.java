package com.yashconsulting.eams.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to define and create a new purchase order")
public class PurchaseOrderCreateRequest {

    @Schema(description = "Unique identifying alphanumeric PO number code", example = "PO-2026-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Purchase order number must not be blank")
    @Size(max = 100, message = "Purchase order number must not exceed 100 characters")
    private String poNumber;

    @Schema(description = "ID of the associated vendor provider", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Vendor ID must not be null")
    private Long vendorId;

    @Schema(description = "Planned date of when order delivery is expected", example = "2026-07-20T12:00:00")
    private LocalDateTime expectedDeliveryDate;

    @Schema(description = "Optional notes or instructions", example = "Rush order delivery requested")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Schema(description = "Collection of line items included in the purchase order", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Purchase order must contain at least one item")
    @Valid
    private List<PurchaseOrderItemCreateRequest> items;
}
