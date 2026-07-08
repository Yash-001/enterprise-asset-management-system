package com.yashconsulting.eams.user.service;

import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.user.dto.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse getUserById(Long id);

    default Page<UserResponse> getAllUsers(Pageable pageable) {
        return getAllUsers(pageable, false);
    }

    Page<UserResponse> getAllUsers(Pageable pageable, boolean includeInactive);

    Page<UserResponse> searchUsers(UserSearchRequest request);

    void deleteUser(Long id);
}
