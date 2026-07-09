package com.yashconsulting.eams.department.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update department details")
public class DepartmentUpdateRequest {

    @Schema(description = "Updated name of the department", example = "Information Technology", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Department name must not be blank")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Department name must not contain leading or trailing spaces")
    private String departmentName;

    @Schema(description = "Updated manager of the department", example = "Jane Doe")
    @Size(max = 100, message = "Manager name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Manager name must not contain leading or trailing spaces")
    private String manager;

    @Schema(description = "Updated detailed description of the department", example = "Responsible for enterprise-wide IT services")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Updated active status flag", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
