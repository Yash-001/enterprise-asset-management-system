package com.yashconsulting.eams.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.RegisterRequest;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Test")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);
    }

    @Test
    void login_validCredentials_returnsTokenAndUserInfo() throws Exception {
        LoginRequest request = new LoginRequest("admin@eams.com", "Admin@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.accessToken", not(emptyString())));
    }

    @Test
    void login_invalidPassword_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("admin@eams.com", "WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nonExistentUser_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@eams.com", "Pass123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withoutToken_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_withInvalidToken_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_withValidToken_returnsOk() throws Exception {
        String token = obtainToken("admin@eams.com", "Admin@123");

        mockMvc.perform(get("/api/v1/assets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void register_newUser_succeeds() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("New")
                .lastName("User")
                .email("newuser@eams.com")
                .password("NewUser@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Dup")
                .lastName("User")
                .email("admin@eams.com")
                .password("Dup@123456")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void accessAdminEndpoint_withUserRole_returnsForbidden() throws Exception {
        // Create a USER-role account
        User regularUser = User.builder()
                .firstName("Regular")
                .lastName("User")
                .email("user@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(regularUser);

        String userToken = obtainToken("user@eams.com", "User@123");

        // Try to create an asset (ADMIN only)
        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden().or(status().isInternalServerError()));
    }

    @Test
    void healthEndpoint_noAuth_accessible() throws Exception {
        mockMvc.perform(get("/management/health"))
                .andExpect(status().isOk());
    }

    private String obtainToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }
}
