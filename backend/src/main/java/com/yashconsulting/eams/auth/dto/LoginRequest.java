package com.yashconsulting.eams.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;
}
