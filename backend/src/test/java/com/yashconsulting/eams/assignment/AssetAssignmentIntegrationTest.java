package com.yashconsulting.eams.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentUpdateRequest;
import com.yashconsulting.eams.assignment.entity.AssetAssignment;
import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import com.yashconsulting.eams.assignment.repository.AssetAssignmentRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AssetAssignmentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetAssignmentRepository assetAssignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;
    private String userToken;

    private Asset availableAsset;
    private Asset assignedAsset;

    @BeforeEach
    void setUp() throws Exception {
        assetAssignmentRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();

        // Seed users
        User admin = User.builder()
                .firstName("Admin")
                .lastName("System")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        User manager = User.builder()
                .firstName("Manager")
                .lastName("System")
                .email("manager@eams.com")
                .password(passwordEncoder.encode("Manager@123"))
                .role(Role.MANAGER)
                .active(true)
                .build();
        userRepository.save(manager);

        User user = User.builder()
                .firstName("User")
                .lastName("System")
                .email("user@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(user);

        // Retrieve tokens
        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        managerToken = obtainAccessToken("manager@eams.com", "Manager@123");
        userToken = obtainAccessToken("user@eams.com", "User@123");

        // Seed assets
        availableAsset = Asset.builder()
                .assetCode("AST-AVAIL")
                .assetName("Available Computer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1500.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        availableAsset = assetRepository.save(availableAsset);

        assignedAsset = Asset.builder()
                .assetCode("AST-ASSN")
                .assetName("Assigned Computer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1500.00))
                .status(AssetStatus.ASSIGNED)
                .active(true)
                .build();
        assignedAsset = assetRepository.save(assignedAsset);
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
    void whenCreateAssignmentAsUser_thenReturns500() throws Exception {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(availableAsset.getId())
                .employeeId(200L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(6))
                .build();

        mockMvc.perform(post("/api/v1/asset-assignments")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenAssignAvailableAsset_thenSuccessAndStatusChangesToAssigned() throws Exception {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(availableAsset.getId())
                .employeeId(200L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(6))
                .remarks("Assigned to Dev Team")
                .build();

        mockMvc.perform(post("/api/v1/asset-assignments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.assetId", is(availableAsset.getId().intValue())))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        // Check asset status was updated in DB
        Asset updatedAsset = assetRepository.findById(availableAsset.getId()).orElseThrow();
        assertEquals(AssetStatus.ASSIGNED, updatedAsset.getStatus());
    }

    @Test
    void whenAssignNonAvailableAsset_thenReturns400() throws Exception {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(assignedAsset.getId())
                .employeeId(200L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(6))
                .build();

        mockMvc.perform(post("/api/v1/asset-assignments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Only AVAILABLE assets can be assigned")));
    }

    @Test
    void whenAssignAssetWithExistingActiveAssignment_thenReturns400() throws Exception {
        // Force-seed active assignment
        AssetAssignment assignment = AssetAssignment.builder()
                .assetId(availableAsset.getId())
                .employeeId(101L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.ACTIVE)
                .build();
        assetAssignmentRepository.save(assignment);

        // Attempt assignment
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(availableAsset.getId())
                .employeeId(202L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(6))
                .build();

        mockMvc.perform(post("/api/v1/asset-assignments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenReturnAsset_thenSuccessAndStatusChangesToAvailable() throws Exception {
        // Seed active assignment
        AssetAssignment assignment = AssetAssignment.builder()
                .assetId(assignedAsset.getId())
                .employeeId(101L)
                .assignedDate(LocalDate.now().minusDays(10))
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.ACTIVE)
                .build();
        assignment = assetAssignmentRepository.save(assignment);

        mockMvc.perform(post("/api/v1/asset-assignments/" + assignment.getId() + "/return")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("remarks", "Returned in good shape"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RETURNED")))
                .andExpect(jsonPath("$.returnedDate", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.remarks", is("Returned in good shape")));

        // Verify asset returned to AVAILABLE
        Asset returnedAsset = assetRepository.findById(assignedAsset.getId()).orElseThrow();
        assertEquals(AssetStatus.AVAILABLE, returnedAsset.getStatus());
    }

    @Test
    void whenUpdateAssignmentExpectedReturnDate_thenReturns200() throws Exception {
        AssetAssignment assignment = AssetAssignment.builder()
                .assetId(assignedAsset.getId())
                .employeeId(101L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.ACTIVE)
                .build();
        assignment = assetAssignmentRepository.save(assignment);

        AssetAssignmentUpdateRequest request = AssetAssignmentUpdateRequest.builder()
                .expectedReturnDate(LocalDate.now().plusMonths(6))
                .remarks("Extended deadline")
                .build();

        mockMvc.perform(put("/api/v1/asset-assignments/" + assignment.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expectedReturnDate", is(LocalDate.now().plusMonths(6).toString())))
                .andExpect(jsonPath("$.remarks", is("Extended deadline")));
    }

    @Test
    void whenDeleteActiveAssignment_thenResetsAssetToAvailableAndDeletes() throws Exception {
        AssetAssignment assignment = AssetAssignment.builder()
                .assetId(assignedAsset.getId())
                .employeeId(101L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.ACTIVE)
                .build();
        assignment = assetAssignmentRepository.save(assignment);

        mockMvc.perform(delete("/api/v1/asset-assignments/" + assignment.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        assertFalse(assetAssignmentRepository.existsById(assignment.getId()));

        // Verify asset was reverted back to AVAILABLE
        Asset reverted = assetRepository.findById(assignedAsset.getId()).orElseThrow();
        assertEquals(AssetStatus.AVAILABLE, reverted.getStatus());
    }

    @Test
    void whenGetHistory_thenReturnsHistoryPageFilteredCorrectly() throws Exception {
        AssetAssignment assignment1 = AssetAssignment.builder()
                .assetId(assignedAsset.getId())
                .employeeId(101L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.ACTIVE)
                .build();
        assetAssignmentRepository.save(assignment1);

        AssetAssignment assignment2 = AssetAssignment.builder()
                .assetId(availableAsset.getId())
                .employeeId(202L)
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AssignmentStatus.RETURNED)
                .build();
        assetAssignmentRepository.save(assignment2);

        // Query by employeeId
        mockMvc.perform(get("/api/v1/asset-assignments/history")
                        .header("Authorization", "Bearer " + userToken)
                        .param("employeeId", "202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].employeeId", is(202)));

        // Query by status
        mockMvc.perform(get("/api/v1/asset-assignments/history")
                        .header("Authorization", "Bearer " + userToken)
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].employeeId", is(101)));
    }
}
