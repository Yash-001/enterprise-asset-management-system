package com.yashconsulting.eams.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing a user profile")
public class UserResponse {

    @Schema(description = "Unique database auto-increment ID", example = "42")
    private Long id;

    @Schema(description = "First name of the user", example = "Jane")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Schema(description = "User login email address", example = "jane.doe@eams.com")
    private String email;

    @Schema(description = "Account active state flag", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-08T18:12:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-08T18:12:36")
    private LocalDateTime updatedAt;
}
