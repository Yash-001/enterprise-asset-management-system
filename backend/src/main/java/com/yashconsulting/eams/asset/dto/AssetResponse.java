package com.yashconsulting.eams.asset.dto;

import com.yashconsulting.eams.asset.entity.AssetStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing an asset details")
public class AssetResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique asset code identifier", example = "AST-2026-001")
    private String assetCode;

    @Schema(description = "Name of the asset", example = "Developer Laptop")
    private String assetName;

    @Schema(description = "Detailed description of the asset", example = "MacBook Pro 16-inch M3 Max 64GB RAM")
    private String description;

    @Schema(description = "Serial number from manufacturer", example = "C02XYZ123ABC")
    private String serialNumber;

    @Schema(description = "Asset manufacturer name", example = "Apple Inc.")
    private String manufacturer;

    @Schema(description = "Asset model name/number", example = "A2991")
    private String model;

    @Schema(description = "Date when the asset was purchased", example = "2026-07-08")
    private LocalDate purchaseDate;

    @Schema(description = "Purchase price of the asset", example = "3499.99")
    private BigDecimal purchasePrice;

    @Schema(description = "Warranty expiry date of the asset", example = "2029-07-08")
    private LocalDate warrantyExpiry;

    @Schema(description = "Current status of the asset", example = "AVAILABLE")
    private AssetStatus status;

    @Schema(description = "Associated department ID", example = "10")
    private Long departmentId;

    @Schema(description = "Associated location ID", example = "20")
    private Long locationId;

    @Schema(description = "Asset active status flag", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-08T18:12:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-08T18:12:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
