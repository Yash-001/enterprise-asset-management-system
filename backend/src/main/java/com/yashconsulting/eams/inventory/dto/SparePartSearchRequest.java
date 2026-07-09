package com.yashconsulting.eams.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching spare parts dynamically with pagination and sorting")
public class SparePartSearchRequest {

    @Schema(description = "Filter by specific part number/SKU (contains, case-insensitive)", example = "SP-HVAC")
    private String partNumber;

    @Schema(description = "Filter by specific part name (contains, case-insensitive)", example = "belt")
    private String partName;

    @Schema(description = "Filter by category classification (contains, case-insensitive)", example = "belt")
    private String category;

    @Schema(description = "Filter by manufacturer name (contains, case-insensitive)", example = "Opti")
    private String manufacturer;

    @Schema(description = "Filter by specific storage location ID", example = "2")
    private Long locationId;

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Schema(description = "Page index (0-indexed, default: 0)", example = "0")
    @Min(value = 0, message = "Page index must be at least 0")
    private Integer page = 0;

    @Schema(description = "Page size (default: 20, max: 100)", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 20;

    @Schema(description = "Field name to sort by", example = "id")
    private String sortBy = "id";

    @Schema(description = "Sort direction (ASC or DESC, default: ASC)", example = "ASC")
    private String sortDirection = "ASC";
}
