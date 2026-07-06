package com.yashconsulting.eams.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
}
