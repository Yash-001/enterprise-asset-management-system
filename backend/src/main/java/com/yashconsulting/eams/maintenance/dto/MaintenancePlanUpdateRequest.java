package com.yashconsulting.eams.maintenance.dto;

import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
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
@Schema(description = "Request body payload to update an existing maintenance plan")
public class MaintenancePlanUpdateRequest {

    @Schema(description = "Name of the maintenance plan", example = "Quarterly HVAC Inspection", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Plan name must not be blank")
    @Size(max = 150, message = "Plan name must not exceed 150 characters")
    @NoLeadingTrailingWhitespace(message = "Plan name must not contain leading or trailing spaces")
    private String planName;

    @Schema(description = "Detailed description of the maintenance plan", example = "Inspection and filter replacement for main HVAC system")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Type of maintenance (PREVENTIVE, CORRECTIVE, CALIBRATION)", example = "PREVENTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Maintenance type must not be null")
    private MaintenanceType maintenanceType;

    @Schema(description = "Frequency interval type (DAYS, WEEKS, MONTHS, YEARS)", example = "MONTHS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Frequency type must not be null")
    private FrequencyType frequencyType;

    @Schema(description = "Frequency numeric value mapping to the type interval", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Frequency value must not be null")
    @Min(value = 1, message = "Frequency value must be at least 1")
    private Integer frequencyValue;

    @Schema(description = "Date of the next scheduled maintenance occurrence", example = "2026-10-09", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Next maintenance date must not be null")
    private LocalDate nextMaintenanceDate;

    @Schema(description = "Date of the last completed maintenance occurrence", example = "2026-07-09")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Estimated duration in hours for the maintenance work", example = "2.5")
    @Positive(message = "Estimated duration must be positive")
    private BigDecimal estimatedDurationHours;

    @Schema(description = "Priority level of the plan (LOW, MEDIUM, HIGH, CRITICAL)", example = "HIGH", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Priority must not be null")
    private MaintenancePriority priority;

    @Schema(description = "Flag indicating whether the maintenance plan is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
