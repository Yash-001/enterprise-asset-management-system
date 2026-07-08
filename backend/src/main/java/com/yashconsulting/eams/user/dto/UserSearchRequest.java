package com.yashconsulting.eams.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request parameters payload for searching users dynamically with pagination and sorting")
public class UserSearchRequest {

    @Schema(description = "Filter by first name (contains, case-insensitive)", example = "Jane")
    private String firstName;

    @Schema(description = "Filter by last name (contains, case-insensitive)", example = "Doe")
    private String lastName;

    @Schema(description = "Filter by email (contains, case-insensitive)", example = "jane.doe@eams.com")
    private String email;

    @Schema(description = "Filter by account active status", example = "true")
    private Boolean active;

    @Schema(description = "Zero-indexed page number for pagination retrieval", example = "0")
    @Min(value = 0, message = "Page number must be zero or positive")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Number of records per page (size limit)", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "Database column property name to sort results by", example = "id")
    @Builder.Default
    private String sortBy = "id";

    @Schema(description = "Sorting order direction ('ASC' or 'DESC')", example = "ASC")
    @Builder.Default
    private String sortDirection = "ASC";
}
