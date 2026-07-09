package com.yashconsulting.eams.vendor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching vendors dynamically with pagination and sorting")
public class VendorSearchRequest {

    @Schema(description = "Filter by specific vendor code (contains, case-insensitive)", example = "VEN")
    private String vendorCode;

    @Schema(description = "Filter by specific vendor name (contains, case-insensitive)", example = "Metal")
    private String vendorName;

    @Schema(description = "Filter by contact person name (contains, case-insensitive)", example = "John")
    private String contactPerson;

    @Schema(description = "Filter by email address (contains, case-insensitive)", example = "contact")
    private String email;

    @Schema(description = "Filter by city (contains, case-insensitive)", example = "Detroit")
    private String city;

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
