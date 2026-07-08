package com.yashconsulting.eams.user.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import com.yashconsulting.eams.user.validation.UniqueEmail;
import com.yashconsulting.eams.user.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to register a new user")
public class UserCreateRequest {

    @Schema(description = "First name of the user", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "First name must not contain leading or trailing spaces")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Last name must not contain leading or trailing spaces")
    private String lastName;

    @Schema(description = "Unique email address for user login", example = "jane.doe@eams.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @NoLeadingTrailingWhitespace(message = "Email must not contain leading or trailing spaces")
    @UniqueEmail(message = "Email address is already in use")
    private String email;

    @Schema(description = "User login password (must meet complexity rules)", example = "Secure@123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password must not be blank")
    @Size(max = 100, message = "Password must not exceed 100 characters")
    @ValidPassword
    private String password;

    @Schema(description = "Flag indicating whether user account is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    @Builder.Default
    private Boolean active = true;
}
