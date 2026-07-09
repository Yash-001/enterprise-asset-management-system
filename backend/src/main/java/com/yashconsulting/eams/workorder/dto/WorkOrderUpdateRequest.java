package com.yashconsulting.eams.workorder.dto;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update an existing work order")
public class WorkOrderUpdateRequest {

    @Schema(description = "Short descriptive title of the work order", example = "Replace HVAC Compressor", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title must not be blank")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    @NoLeadingTrailingWhitespace(message = "Title must not contain leading or trailing spaces")
    private String title;

    @Schema(description = "Detailed description of the work to be performed", example = "Technician needs to replace the compressor and refill refrigerant.")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Name or ID of the assigned technician", example = "John Doe")
    @Size(max = 150, message = "Assigned technician must not exceed 150 characters")
    private String assignedTechnician;

    @Schema(description = "Priority level of the work order", example = "HIGH", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Priority must not be null")
    private MaintenancePriority priority;

    @Schema(description = "Status of the work order", example = "ASSIGNED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Status must not be null")
    private WorkOrderStatus status;

    @Schema(description = "Date when the work is scheduled to be performed", example = "2026-07-10")
    private LocalDate scheduledDate;

    @Schema(description = "Date when the work actually started", example = "2026-07-10")
    private LocalDate startDate;

    @Schema(description = "Date when the work was completed", example = "2026-07-11")
    private LocalDate completionDate;

    @Schema(description = "Estimated duration in hours", example = "4.50")
    @PositiveOrZero(message = "Estimated hours must be positive or zero")
    private BigDecimal estimatedHours;

    @Schema(description = "Actual duration in hours", example = "5.00")
    @PositiveOrZero(message = "Actual hours must be positive or zero")
    private BigDecimal actualHours;

    @Schema(description = "Additional remarks or technician notes", example = "Successfully completed, tested operations.")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Schema(description = "Flag indicating whether the work order is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
