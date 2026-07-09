package com.yashconsulting.eams.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing a department profile")
public class DepartmentResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique code identifying the department", example = "DEP-IT-10")
    private String departmentCode;

    @Schema(description = "Name of the department", example = "Information Technology")
    private String departmentName;

    @Schema(description = "Manager of the department", example = "Jane Doe")
    private String manager;

    @Schema(description = "Detailed description of the department", example = "Responsible for enterprise-wide IT services")
    private String description;

    @Schema(description = "Department active status flag", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-09T19:12:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-09T19:12:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
