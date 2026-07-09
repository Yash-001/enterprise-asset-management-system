package com.yashconsulting.eams.maintenance.dto;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching maintenance plans dynamically with pagination and sorting")
public class MaintenancePlanSearchRequest {

    @Schema(description = "Filter by specific asset ID", example = "1")
    private Long assetId;

    @Schema(description = "Filter by plan code (contains, case-insensitive)", example = "HVAC")
    private String planCode;

    @Schema(description = "Filter by plan name (contains, case-insensitive)", example = "Inspection")
    private String planName;

    @Schema(description = "Filter by maintenance type (PREVENTIVE, CORRECTIVE, CALIBRATION)", example = "PREVENTIVE")
    private MaintenanceType maintenanceType;

    @Schema(description = "Filter by priority level (LOW, MEDIUM, HIGH, CRITICAL)", example = "HIGH")
    private MaintenancePriority priority;

    @Schema(description = "Filter by maintenance status (SCHEDULED, IN_PROGRESS, COMPLETED, OVERDUE, CANCELLED)", example = "SCHEDULED")
    private MaintenanceStatus status;

    @Schema(description = "Filter by active status", example = "true")
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
