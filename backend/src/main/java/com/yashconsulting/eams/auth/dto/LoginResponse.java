package com.yashconsulting.eams.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
}
