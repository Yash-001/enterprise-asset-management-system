package com.yashconsulting.eams.workorder.dto;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching work orders dynamically with pagination and sorting")
public class WorkOrderSearchRequest {

    @Schema(description = "Filter by specific work order number (contains, case-insensitive)", example = "WO-2026")
    private String workOrderNumber;

    @Schema(description = "Filter by specific work order title (contains, case-insensitive)", example = "HVAC")
    private String title;

    @Schema(description = "Filter by specific asset ID", example = "1")
    private Long assetId;

    @Schema(description = "Filter by associated maintenance plan ID", example = "1")
    private Long maintenancePlanId;

    @Schema(description = "Filter by assigned technician name (contains, case-insensitive)", example = "John")
    private String assignedTechnician;

    @Schema(description = "Filter by priority level (LOW, MEDIUM, HIGH, CRITICAL)", example = "HIGH")
    private MaintenancePriority priority;

    @Schema(description = "Filter by work order status (REQUESTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)", example = "ASSIGNED")
    private WorkOrderStatus status;

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
