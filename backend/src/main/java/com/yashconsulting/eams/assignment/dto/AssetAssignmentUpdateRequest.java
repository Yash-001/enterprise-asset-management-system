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
@Schema(description = "Request body payload to update asset assignment metadata")
public class AssetAssignmentUpdateRequest {

    @Schema(description = "Updated expected date the asset should be returned", example = "2027-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Expected return date must not be null")
    private LocalDate expectedReturnDate;

    @Schema(description = "Updated remarks regarding the assignment", example = "Extension granted for development testing")
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
