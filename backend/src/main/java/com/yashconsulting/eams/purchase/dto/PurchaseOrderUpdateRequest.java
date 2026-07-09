package com.yashconsulting.eams.purchase.dto;

import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Request body payload to update an existing purchase order")
public class PurchaseOrderUpdateRequest {

    @Schema(description = "Purchase order status workflow state", example = "APPROVED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Status must not be null")
    private PurchaseOrderStatus status;

    @Schema(description = "Planned date of when order delivery is expected", example = "2026-07-20T12:00:00")
    private LocalDateTime expectedDeliveryDate;

    @Schema(description = "Optional notes or comments", example = "Supplier confirmed delivery schedule")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Schema(description = "Flag indicating whether the purchase order is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;

    @Schema(description = "Updated collection of line items (only modifiable when status is DRAFT)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Purchase order must contain at least one item")
    @Valid
    private List<PurchaseOrderItemCreateRequest> items;
}
