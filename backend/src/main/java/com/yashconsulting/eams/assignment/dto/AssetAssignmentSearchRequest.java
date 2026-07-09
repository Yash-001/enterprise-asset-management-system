package com.yashconsulting.eams.assignment.dto;

import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for retrieving assignment history dynamically with pagination and sorting")
public class AssetAssignmentSearchRequest {

    @Schema(description = "Filter by specific asset ID", example = "1")
    private Long assetId;

    @Schema(description = "Filter by specific employee ID", example = "101")
    private Long employeeId;

    @Schema(description = "Filter by assignment status (ACTIVE or RETURNED)", example = "ACTIVE")
    private AssignmentStatus status;

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

    @Schema(description = "Sorting order direction ('ASC' or 'DESC')", example = "DESC")
    @Builder.Default
    private String sortDirection = "DESC";
}
