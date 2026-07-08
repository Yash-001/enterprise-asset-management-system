package com.yashconsulting.eams.asset.dto;

import com.yashconsulting.eams.asset.entity.AssetStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching assets dynamically with pagination and sorting")
public class AssetSearchRequest {

    @Schema(description = "Filter by asset code (contains, case-insensitive)", example = "AST-2026")
    private String assetCode;

    @Schema(description = "Filter by asset name (contains, case-insensitive)", example = "Laptop")
    private String assetName;

    @Schema(description = "Filter by serial number (contains, case-insensitive)", example = "XYZ123")
    private String serialNumber;

    @Schema(description = "Filter by manufacturer name (contains, case-insensitive)", example = "Apple")
    private String manufacturer;

    @Schema(description = "Filter by model name/number (contains, case-insensitive)", example = "A2991")
    private String model;

    @Schema(description = "Filter by asset current status", example = "AVAILABLE")
    private AssetStatus status;

    @Schema(description = "Filter by associated department ID", example = "10")
    private Long departmentId;

    @Schema(description = "Filter by associated location ID", example = "20")
    private Long locationId;

    @Schema(description = "Filter by asset active status", example = "true")
    private Boolean active;

    @Schema(description = "Zero-indexed page number for pagination retrieval", example = "0")
    @Min(value = 0, message = "Page number must be zero or positive")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Number of records per page (size limit)", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "Database column property name to sort results by", example = "id")
    @Builder.Default
    private String sortBy = "id";

    @Schema(description = "Sorting order direction ('ASC' or 'DESC')", example = "ASC")
    @Builder.Default
    private String sortDirection = "ASC";
}
