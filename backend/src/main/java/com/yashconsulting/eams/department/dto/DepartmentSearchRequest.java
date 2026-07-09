package com.yashconsulting.eams.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching departments dynamically with pagination and sorting")
public class DepartmentSearchRequest {

    @Schema(description = "Filter by department code (contains, case-insensitive)", example = "DEP-IT")
    private String departmentCode;

    @Schema(description = "Filter by department name (contains, case-insensitive)", example = "Technology")
    private String departmentName;

    @Schema(description = "Filter by department manager name (contains, case-insensitive)", example = "Jane")
    private String manager;

    @Schema(description = "Filter by department active status", example = "true")
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
