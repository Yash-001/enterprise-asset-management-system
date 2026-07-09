package com.yashconsulting.eams.workorder.dto;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
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
@Schema(description = "Response payload representing work order details")
public class WorkOrderResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique alphanumeric identifier for the work order", example = "WO-2026-0001")
    private String workOrderNumber;

    @Schema(description = "ID of the associated asset", example = "1")
    private Long assetId;

    @Schema(description = "ID of the associated maintenance plan", example = "1")
    private Long maintenancePlanId;

    @Schema(description = "Short descriptive title of the work order", example = "Replace HVAC Compressor")
    private String title;

    @Schema(description = "Detailed description of the work to be performed", example = "Technician needs to replace the compressor and refill refrigerant.")
    private String description;

    @Schema(description = "Name or ID of the assigned technician", example = "John Doe")
    private String assignedTechnician;

    @Schema(description = "Priority level of the work order", example = "HIGH")
    private MaintenancePriority priority;

    @Schema(description = "Status of the work order", example = "ASSIGNED")
    private WorkOrderStatus status;

    @Schema(description = "Date when the work is scheduled to be performed", example = "2026-07-10")
    private LocalDate scheduledDate;

    @Schema(description = "Date when the work actually started", example = "2026-07-10")
    private LocalDate startDate;

    @Schema(description = "Date when the work was completed", example = "2026-07-11")
    private LocalDate completionDate;

    @Schema(description = "Estimated duration in hours", example = "4.50")
    private BigDecimal estimatedHours;

    @Schema(description = "Actual duration in hours", example = "5.00")
    private BigDecimal actualHours;

    @Schema(description = "Additional remarks or technician notes", example = "Successfully completed, tested operations.")
    private String remarks;

    @Schema(description = "Flag indicating whether the work order is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-09T20:26:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-09T20:26:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
