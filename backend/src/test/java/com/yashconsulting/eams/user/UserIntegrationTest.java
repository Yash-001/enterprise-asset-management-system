package com.yashconsulting.eams.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.dto.UserCreateRequest;
import com.yashconsulting.eams.user.dto.UserResponse;
import com.yashconsulting.eams.user.dto.UserUpdateRequest;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        // Seed default admin user
        User admin = User.builder()
                .firstName("Admin")
                .lastName("System")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        // Seed default manager user
        User manager = User.builder()
                .firstName("Manager")
                .lastName("System")
                .email("manager@eams.com")
                .password(passwordEncoder.encode("Manager@123"))
                .role(Role.MANAGER)
                .active(true)
                .build();
        userRepository.save(manager);

        // Login as admin and manager to get JWTs
        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        managerToken = obtainAccessToken("manager@eams.com", "Manager@123");
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        return loginResponse.getAccessToken();
    }

    @Test
    void whenAccessSecuredEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAccessAdminEndpointWithManagerToken_thenReturns500() throws Exception {
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName("New")
                .lastName("User")
                .email("new@eams.com")
                .password("NewPassword@123")
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateUserWithAdminToken_thenReturns201AndSavesToDb() throws Exception {
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@eams.com")
                .password("Secure@123")
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@eams.com")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void whenCreateUserWithInvalidFields_thenReturns400() throws Exception {
        // Leading/trailing spaces reject check
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName(" Jane ")
                .lastName("Doe")
                .email("jane.doe@eams.com")
                .password("Secure@123")
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must not contain leading or trailing spaces")));
    }

    @Test
    void whenGetUserById_thenReturnsUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@eams.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .active(true)
                .build();
        user = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/" + user.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.smith@eams.com")));
    }

    @Test
    void whenUpdateUser_thenReturnsUpdatedUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@eams.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .active(true)
                .build();
        user = userRepository.save(user);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstName("Johnny")
                .lastName("Smithy")
                .active(false)
                .build();

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Johnny")))
                .andExpect(jsonPath("$.lastName", is("Smithy")))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    void whenDeleteUser_thenSoftDeletesUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@eams.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .active(true)
                .build();
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/" + user.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify it was soft deleted (active = false) in DB
        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertFalse(deletedUser.getActive());

        // Verify that authentication fails for this deactivated user
        LoginRequest loginRequest = new LoginRequest("john.smith@eams.com", "Password@123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenSearchUsers_thenFiltersCorrectly() throws Exception {
        User user1 = User.builder()
                .firstName("Alice")
                .lastName("Brown")
                .email("alice.brown@eams.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .firstName("Bob")
                .lastName("Green")
                .email("bob.green@eams.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .active(false)
                .build();
        userRepository.save(user2);

        // Search for active users (should default to active=true and return only Alice)
        mockMvc.perform(get("/api/v1/users/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("firstName", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("alice.brown@eams.com")));

        // Search explicitly with active=false (should return Bob)
        mockMvc.perform(get("/api/v1/users/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("bob.green@eams.com")));
    }
}
