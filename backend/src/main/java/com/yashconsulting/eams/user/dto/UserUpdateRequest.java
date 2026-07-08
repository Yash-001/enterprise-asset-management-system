package com.yashconsulting.eams.user.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "First name must not contain leading or trailing spaces")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Last name must not contain leading or trailing spaces")
    private String lastName;

    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
