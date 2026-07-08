package com.yashconsulting.eams.user.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update user details")
public class UserUpdateRequest {

    @Schema(description = "Updated first name of the user", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "First name must not contain leading or trailing spaces")
    private String firstName;

    @Schema(description = "Updated last name of the user", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Last name must not contain leading or trailing spaces")
    private String lastName;

    @Schema(description = "Flag indicating whether user account is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
