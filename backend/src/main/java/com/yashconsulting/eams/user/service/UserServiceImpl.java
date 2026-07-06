package com.yashconsulting.eams.user.service;

import com.yashconsulting.eams.exception.EmailAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.user.dto.UserSearchRequest;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.mapper.UserMapper;
import com.yashconsulting.eams.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String sanitizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        
        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + sanitizedEmail);
        }

        User user = userMapper.toEntity(request);
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserByIdOrThrow(id);

        userMapper.updateEntity(request, user);
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = getUserByIdOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(UserSearchRequest request) {
        // TODO: Implement dynamic specifications search in next sprint
        throw new UnsupportedOperationException("Dynamic search using specifications is not implemented yet.");
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserByIdOrThrow(id);
        
        if (Boolean.FALSE.equals(user.getActive())) {
            return;
        }

        user.setActive(false);
        userRepository.save(user);
    }

    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
