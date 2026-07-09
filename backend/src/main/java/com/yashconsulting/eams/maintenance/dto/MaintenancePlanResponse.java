package com.yashconsulting.eams.maintenance.dto;

import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
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
@Schema(description = "Response payload representing a preventive maintenance plan details")
public class MaintenancePlanResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "ID of the associated asset", example = "1")
    private Long assetId;

    @Schema(description = "Unique code identifying the maintenance plan", example = "MP-HVAC-001")
    private String planCode;

    @Schema(description = "Name of the maintenance plan", example = "Quarterly HVAC Inspection")
    private String planName;

    @Schema(description = "Detailed description of the maintenance plan", example = "Inspection and filter replacement for main HVAC system")
    private String description;

    @Schema(description = "Type of maintenance", example = "PREVENTIVE")
    private MaintenanceType maintenanceType;

    @Schema(description = "Frequency interval type", example = "MONTHS")
    private FrequencyType frequencyType;

    @Schema(description = "Frequency numeric value mapping to the type interval", example = "3")
    private Integer frequencyValue;

    @Schema(description = "Date of the next scheduled maintenance occurrence", example = "2026-10-09")
    private LocalDate nextMaintenanceDate;

    @Schema(description = "Date of the last completed maintenance occurrence", example = "2026-07-09")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Estimated duration in hours for the maintenance work", example = "2.5")
    private BigDecimal estimatedDurationHours;

    @Schema(description = "Priority level of the plan", example = "HIGH")
    private MaintenancePriority priority;

    @Schema(description = "Maintenance plan active status flag", example = "true")
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
