package com.yashconsulting.eams.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching locations dynamically with pagination and sorting")
public class LocationSearchRequest {

    @Schema(description = "Filter by location code (contains, case-insensitive)", example = "LOC-HQ")
    private String locationCode;

    @Schema(description = "Filter by location name (contains, case-insensitive)", example = "Conference")
    private String locationName;

    @Schema(description = "Filter by building (contains, case-insensitive)", example = "Tower")
    private String building;

    @Schema(description = "Filter by floor (contains, case-insensitive)", example = "4th")
    private String floor;

    @Schema(description = "Filter by room (contains, case-insensitive)", example = "402")
    private String room;

    @Schema(description = "Filter by location active status", example = "true")
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
