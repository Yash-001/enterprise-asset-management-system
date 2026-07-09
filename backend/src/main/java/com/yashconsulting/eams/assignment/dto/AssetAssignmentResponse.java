package com.yashconsulting.eams.assignment.dto;

import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing an asset assignment profile")
public class AssetAssignmentResponse {

    @Schema(description = "Unique database assignment ID", example = "1")
    private Long id;

    @Schema(description = "ID of the assigned asset", example = "1")
    private Long assetId;

    @Schema(description = "ID of the employee assigned to the asset", example = "101")
    private Long employeeId;

    @Schema(description = "Date the assignment commenced", example = "2026-07-09")
    private LocalDate assignedDate;

    @Schema(description = "Expected date for asset return", example = "2026-12-31")
    private LocalDate expectedReturnDate;

    @Schema(description = "Actual date the asset was returned", example = "2026-12-30")
    private LocalDate returnedDate;

    @Schema(description = "Remarks/notes regarding the assignment", example = "Assigned for development purposes")
    private String remarks;

    @Schema(description = "Assignment active/returned status flag", example = "ACTIVE")
    private AssignmentStatus status;
}
