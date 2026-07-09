package com.yashconsulting.eams.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to assign an asset to an employee")
public class AssetAssignmentCreateRequest {

    @Schema(description = "ID of the asset to assign", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Asset ID must not be null")
    private Long assetId;

    @Schema(description = "ID of the employee receiving the asset", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Employee ID must not be null")
    private Long employeeId;

    @Schema(description = "Date the asset is assigned", example = "2026-07-09", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Assigned date must not be null")
    private LocalDate assignedDate;

    @Schema(description = "Expected date the asset should be returned", example = "2026-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Expected return date must not be null")
    private LocalDate expectedReturnDate;

    @Schema(description = "Remarks/notes regarding the assignment", example = "Assigned for development purposes")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
