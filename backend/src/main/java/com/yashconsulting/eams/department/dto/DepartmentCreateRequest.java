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
@Schema(description = "Request body payload to register a new department")
public class DepartmentCreateRequest {

    @Schema(description = "Unique code identifying the department", example = "DEP-IT-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Department code must not be blank")
    @Size(max = 100, message = "Department code must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Department code must not contain leading or trailing spaces")
    private String departmentCode;

    @Schema(description = "Name of the department", example = "Information Technology", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Department name must not be blank")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Department name must not contain leading or trailing spaces")
    private String departmentName;

    @Schema(description = "Manager of the department", example = "Jane Doe")
    @Size(max = 100, message = "Manager name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Manager name must not contain leading or trailing spaces")
    private String manager;

    @Schema(description = "Detailed description of the department", example = "Responsible for enterprise-wide IT services")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Flag indicating whether department is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    @Builder.Default
    private Boolean active = true;
}
