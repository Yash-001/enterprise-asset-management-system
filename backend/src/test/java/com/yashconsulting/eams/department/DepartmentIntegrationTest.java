package com.yashconsulting.eams.department;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.department.dto.DepartmentCreateRequest;
import com.yashconsulting.eams.department.dto.DepartmentUpdateRequest;
import com.yashconsulting.eams.department.entity.Department;
import com.yashconsulting.eams.department.repository.DepartmentRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class DepartmentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    void setUp() throws Exception {
        departmentRepository.deleteAll();
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

        // Login to retrieve tokens
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
    void whenAccessSecuredDepartmentEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateDepartmentAsAdmin_thenReturns201() throws Exception {
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .departmentCode("DEP-1")
                .departmentName("Engineering")
                .manager("John Doe")
                .description("Core product development")
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.departmentCode", is("DEP-1")))
                .andExpect(jsonPath("$.departmentName", is("Engineering")))
                .andExpect(jsonPath("$.manager", is("John Doe")));
    }

    @Test
    void whenCreateDepartmentAsManager_thenReturns500() throws Exception {
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .departmentCode("DEP-2")
                .departmentName("QA")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateDepartmentWithDuplicateCode_thenReturns409() throws Exception {
        Department existing = Department.builder()
                .departmentCode("DEP-DUP")
                .departmentName("Original Dept")
                .active(true)
                .build();
        departmentRepository.save(existing);

        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .departmentCode("DEP-DUP")
                .departmentName("Duplicate Dept")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("is already registered")));
    }

    @Test
    void whenCreateDepartmentWithWhitespace_thenTrimmed() throws Exception {
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .departmentCode("DEP-SPACE")
                .departmentName("Clean Name")
                .manager("Clean Manager")
                .description("   Clean Desc   ")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Clean Desc")));
    }

    @Test
    void whenCreateDepartmentWithBlankRequiredFields_thenReturns400() throws Exception {
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .departmentCode("   ")
                .departmentName("")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateDepartment_thenReturns200() throws Exception {
        Department existing = Department.builder()
                .departmentCode("DEP-TO-UPDATE")
                .departmentName("Old Name")
                .active(true)
                .build();
        existing = departmentRepository.save(existing);

        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .departmentName("New Name")
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/departments/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName", is("New Name")))
                .andExpect(jsonPath("$.departmentCode", is("DEP-TO-UPDATE")));
    }

    @Test
    void whenDeleteDepartment_thenSoftDeletes() throws Exception {
        Department existing = Department.builder()
                .departmentCode("DEP-TO-DELETE")
                .departmentName("Delete Me")
                .active(true)
                .build();
        existing = departmentRepository.save(existing);

        mockMvc.perform(delete("/api/v1/departments/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Department deleted = departmentRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchDepartments_thenFiltersCorrectly() throws Exception {
        Department dep1 = Department.builder()
                .departmentCode("DEP-FIN")
                .departmentName("Finance Dept")
                .manager("Alice")
                .active(true)
                .build();
        departmentRepository.save(dep1);

        Department dep2 = Department.builder()
                .departmentCode("DEP-HR")
                .departmentName("Human Resources")
                .manager("Bob")
                .active(true)
                .build();
        departmentRepository.save(dep2);

        // Search by departmentName
        mockMvc.perform(get("/api/v1/departments/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("departmentName", "Finance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].departmentCode", is("DEP-FIN")));

        // Search by manager
        mockMvc.perform(get("/api/v1/departments/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("manager", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].departmentCode", is("DEP-HR")));
    }
}
