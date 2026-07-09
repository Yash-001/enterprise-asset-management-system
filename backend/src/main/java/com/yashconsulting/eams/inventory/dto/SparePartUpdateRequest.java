package com.yashconsulting.eams.inventory.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update an existing spare part")
public class SparePartUpdateRequest {

    @Schema(description = "Name of the spare part", example = "HVAC Drive Belt V-400", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Part name must not be blank")
    @Size(max = 150, message = "Part name must not exceed 150 characters")
    @NoLeadingTrailingWhitespace(message = "Part name must not contain leading or trailing spaces")
    private String partName;

    @Schema(description = "Detailed description of the spare part", example = "Heavy duty cogged V-belt for industrial ventilation units.")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Manufacturer of the spare part", example = "Optibelt")
    @Size(max = 150, message = "Manufacturer must not exceed 150 characters")
    private String manufacturer;

    @Schema(description = "Classification/Category of the spare part", example = "Belts & Pulleys")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Schema(description = "Standard unit of measurement", example = "PCS")
    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @Schema(description = "Minimum threshold count for safety stock", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Minimum stock must not be null")
    @Min(value = 0, message = "Minimum stock must be 0 or greater")
    private Integer minimumStock;

    @Schema(description = "Maximum capacity threshold count", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Maximum stock must not be null")
    @Min(value = 0, message = "Maximum stock must be 0 or greater")
    private Integer maximumStock;

    @Schema(description = "Current stock inventory count available", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Current stock must not be null")
    @Min(value = 0, message = "Current stock must be 0 or greater")
    private Integer currentStock;

    @Schema(description = "Unit cost price of a single item", example = "45.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Unit cost must not be null")
    @PositiveOrZero(message = "Unit cost must be positive or zero")
    private BigDecimal unitCost;

    @Schema(description = "Optional ID referencing an external supplier", example = "10")
    private Long supplierId;

    @Schema(description = "Optional ID referencing the storage location", example = "2")
    private Long locationId;

    @Schema(description = "Flag indicating whether the spare part is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
