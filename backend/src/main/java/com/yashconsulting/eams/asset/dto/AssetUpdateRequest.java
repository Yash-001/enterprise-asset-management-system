package com.yashconsulting.eams.asset.dto;

import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update asset details")
public class AssetUpdateRequest {

    @Schema(description = "Updated name of the asset", example = "Developer Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Asset name must not be blank")
    @Size(max = 100, message = "Asset name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Asset name must not contain leading or trailing spaces")
    private String assetName;

    @Schema(description = "Updated detailed description of the asset", example = "MacBook Pro 16-inch M3 Max 64GB RAM")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Updated serial number from manufacturer", example = "C02XYZ123ABC")
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;

    @Schema(description = "Updated asset manufacturer name", example = "Apple Inc.")
    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    private String manufacturer;

    @Schema(description = "Updated asset model name/number", example = "A2991")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    @Schema(description = "Updated date when the asset was purchased", example = "2026-07-08", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Purchase date must not be null")
    @PastOrPresent(message = "Purchase date must be in the past or present")
    private LocalDate purchaseDate;

    @Schema(description = "Updated purchase price of the asset", example = "3499.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Purchase price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price must be positive")
    private BigDecimal purchasePrice;

    @Schema(description = "Updated warranty expiry date of the asset", example = "2029-07-08")
    private LocalDate warrantyExpiry;

    @Schema(description = "Updated current status of the asset", example = "AVAILABLE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Asset status must not be null")
    private AssetStatus status;

    @Schema(description = "Updated associated department ID", example = "10")
    private Long departmentId;

    @Schema(description = "Updated associated location ID", example = "20")
    private Long locationId;

    @Schema(description = "Updated flag indicating whether asset is active in systems", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
