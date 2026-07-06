package com.yashconsulting.eams.user.mapper;

import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .firstName(dto.getFirstName() != null ? dto.getFirstName().trim() : null)
                .lastName(dto.getLastName() != null ? dto.getLastName().trim() : null)
                .email(dto.getEmail() != null ? dto.getEmail().trim() : null)
                .password(dto.getPassword()) // Do not trim password
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) {
            return null;
        }

        return UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(UserUpdateRequest dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            entity.setFirstName(dto.getFirstName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            entity.setLastName(dto.getLastName().trim());
        }

        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
