package com.yashconsulting.eams.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing spare part details")
public class SparePartResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique alphanumeric identifier/SKU for the spare part", example = "SP-HVAC-BELT")
    private String partNumber;

    @Schema(description = "Name of the spare part", example = "HVAC Drive Belt V-400")
    private String partName;

    @Schema(description = "Detailed description of the spare part", example = "Heavy duty cogged V-belt for industrial ventilation units.")
    private String description;

    @Schema(description = "Manufacturer of the spare part", example = "Optibelt")
    private String manufacturer;

    @Schema(description = "Classification/Category of the spare part", example = "Belts & Pulleys")
    private String category;

    @Schema(description = "Standard unit of measurement", example = "PCS")
    private String unitOfMeasure;

    @Schema(description = "Minimum threshold count for safety stock", example = "5")
    private Integer minimumStock;

    @Schema(description = "Maximum capacity threshold count", example = "50")
    private Integer maximumStock;

    @Schema(description = "Current stock inventory count available", example = "12")
    private Integer currentStock;

    @Schema(description = "Unit cost price of a single item", example = "45.50")
    private BigDecimal unitCost;

    @Schema(description = "Optional ID referencing an external supplier", example = "10")
    private Long supplierId;

    @Schema(description = "Optional ID referencing the storage location", example = "2")
    private Long locationId;

    @Schema(description = "Flag indicating whether the spare part is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-09T20:26:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-09T20:26:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
