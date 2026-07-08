package com.yashconsulting.eams.user.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import com.yashconsulting.eams.user.validation.UniqueEmail;
import com.yashconsulting.eams.user.validation.ValidPassword;
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
public class UserCreateRequest {

    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "First name must not contain leading or trailing spaces")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Last name must not contain leading or trailing spaces")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @NoLeadingTrailingWhitespace(message = "Email must not contain leading or trailing spaces")
    @UniqueEmail(message = "Email address is already in use")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(max = 100, message = "Password must not exceed 100 characters")
    @ValidPassword
    private String password;

    @NotNull(message = "Active status must not be null")
    @Builder.Default
    private Boolean active = true;
}
