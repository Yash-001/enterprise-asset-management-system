package com.yashconsulting.eams.user.service;

import com.yashconsulting.eams.exception.EmailAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.mapper.UserMapper;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_withUniqueEmail_succeeds() {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("  Test@Example.COM  ")
                .password("SecurePass123!")
                .firstName("John")
                .lastName("Doe")
                .build();

        User user = User.builder().build();
        User savedUser = User.builder().id(1L).email("test@example.com").build();
        UserResponse response = UserResponse.builder().id(1L).email("test@example.com").build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("SecurePass123!");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_withDuplicateEmail_throwsEmailAlreadyExistsException() {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("existing@example.com")
                .password("pass")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void createUser_sanitizesEmail_toLowerCaseTrimmed() {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("  UPPER@CASE.COM  ")
                .password("pass")
                .build();

        when(userRepository.existsByEmail("upper@case.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(User.builder().build());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(User.builder().id(1L).build());
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().id(1L).build());

        userService.createUser(request);

        verify(userRepository).existsByEmail("upper@case.com");
    }

    @Test
    void updateUser_existingUser_succeeds() {
        User user = User.builder().id(1L).email("test@example.com").active(true).build();
        UserUpdateRequest request = UserUpdateRequest.builder().firstName("Jane").build();
        UserResponse response = UserResponse.builder().id(1L).firstName("Jane").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUser(1L, request);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(userMapper).updateEntity(request, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(99L, UserUpdateRequest.builder().build()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_existingUser_returnsResponse() {
        User user = User.builder().id(1L).email("test@example.com").build();
        UserResponse response = UserResponse.builder().id(1L).email("test@example.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers_excludeInactive_callsFindAllByActiveTrue() {
        Page<User> page = new PageImpl<>(List.of(User.builder().id(1L).build()));
        when(userRepository.findAllByActiveTrue(any(Pageable.class))).thenReturn(page);
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().id(1L).build());

        Page<UserResponse> result = userService.getAllUsers(Pageable.unpaged(), false);

        assertFalse(result.isEmpty());
        verify(userRepository).findAllByActiveTrue(any(Pageable.class));
        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllUsers_includeInactive_callsFindAll() {
        Page<User> page = new PageImpl<>(List.of(User.builder().id(1L).build()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().id(1L).build());

        Page<UserResponse> result = userService.getAllUsers(Pageable.unpaged(), true);

        assertFalse(result.isEmpty());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void deleteUser_activeUser_softDeletes() {
        User user = User.builder().id(1L).active(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_alreadyInactive_doesNotSave() {
        User user = User.builder().id(1L).active(false).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_nonExisting_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
    }
}
