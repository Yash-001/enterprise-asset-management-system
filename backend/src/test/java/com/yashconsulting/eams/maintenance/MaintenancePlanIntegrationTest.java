package com.yashconsulting.eams.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanCreateRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanUpdateRequest;
import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class MaintenancePlanIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;

    private Asset seededAsset;

    @BeforeEach
    void setUp() throws Exception {
        maintenancePlanRepository.deleteAll();
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

        // Fetch tokens
        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        managerToken = obtainAccessToken("manager@eams.com", "Manager@123");

        // Seed asset
        seededAsset = Asset.builder()
                .assetCode("AST-PM-101")
                .assetName("Production Machine 101")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(25000.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        seededAsset = assetRepository.save(seededAsset);
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
    void whenAccessSecuredMaintenancePlanEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/maintenance-plans"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateMaintenancePlanAsAdmin_thenReturns201() throws Exception {
        MaintenancePlanCreateRequest request = MaintenancePlanCreateRequest.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-PM-01")
                .planName("Monthly Machine Lube")
                .description("Inject lubricants into all hinges")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.MONTHLY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now().plusMonths(1))
                .estimatedDurationHours(BigDecimal.valueOf(1.5))
                .priority(MaintenancePriority.MEDIUM)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/maintenance-plans")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.planCode", is("MP-PM-01")))
                .andExpect(jsonPath("$.planName", is("Monthly Machine Lube")))
                .andExpect(jsonPath("$.estimatedDurationHours", is(1.5)));
    }

    @Test
    void whenCreateMaintenancePlanAsManager_thenReturns500() throws Exception {
        MaintenancePlanCreateRequest request = MaintenancePlanCreateRequest.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-PM-02")
                .planName("Weekly Filter Wash")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.WEEKLY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now().plusWeeks(1))
                .priority(MaintenancePriority.LOW)
                .build();

        mockMvc.perform(post("/api/v1/maintenance-plans")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateMaintenancePlanWithDuplicateCode_thenReturns409() throws Exception {
        MaintenancePlan existing = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-DUP")
                .planName("First Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(5)
                .nextMaintenanceDate(LocalDate.now().plusDays(5))
                .priority(MaintenancePriority.LOW)
                .active(true)
                .build();
        maintenancePlanRepository.save(existing);

        MaintenancePlanCreateRequest request = MaintenancePlanCreateRequest.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-DUP")
                .planName("Second Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(10)
                .nextMaintenanceDate(LocalDate.now().plusDays(10))
                .priority(MaintenancePriority.MEDIUM)
                .build();

        mockMvc.perform(post("/api/v1/maintenance-plans")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    void whenCreateMaintenancePlanWithBlankRequiredFields_thenReturns400() throws Exception {
        MaintenancePlanCreateRequest request = MaintenancePlanCreateRequest.builder()
                .planCode("   ")
                .planName("")
                .build();

        mockMvc.perform(post("/api/v1/maintenance-plans")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateMaintenancePlan_thenReturns200() throws Exception {
        MaintenancePlan existing = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-TO-UPDATE")
                .planName("Old Plan Name")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(5)
                .nextMaintenanceDate(LocalDate.now().plusDays(5))
                .priority(MaintenancePriority.LOW)
                .active(true)
                .build();
        existing = maintenancePlanRepository.save(existing);

        MaintenancePlanUpdateRequest request = MaintenancePlanUpdateRequest.builder()
                .planName("New Plan Name")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(5)
                .nextMaintenanceDate(LocalDate.now().plusDays(5))
                .priority(MaintenancePriority.LOW)
                .status(MaintenanceStatus.SCHEDULED)
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/maintenance-plans/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName", is("New Plan Name")))
                .andExpect(jsonPath("$.planCode", is("MP-TO-UPDATE")));
    }

    @Test
    void whenDeleteMaintenancePlan_thenSoftDeletesPlan() throws Exception {
        MaintenancePlan existing = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-TO-DELETE")
                .planName("Temporary Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(5)
                .nextMaintenanceDate(LocalDate.now().plusDays(5))
                .priority(MaintenancePriority.LOW)
                .active(true)
                .build();
        existing = maintenancePlanRepository.save(existing);

        mockMvc.perform(delete("/api/v1/maintenance-plans/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        MaintenancePlan deleted = maintenancePlanRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchMaintenancePlans_thenFiltersCorrectly() throws Exception {
        MaintenancePlan plan1 = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-HEAT")
                .planName("Boiler Flush")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.YEARLY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now().plusYears(1))
                .priority(MaintenancePriority.HIGH)
                .active(true)
                .build();
        maintenancePlanRepository.save(plan1);

        MaintenancePlan plan2 = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-CALIB")
                .planName("Sensor Calibration")
                .maintenanceType(MaintenanceType.CALIBRATION)
                .frequencyType(FrequencyType.MONTHLY)
                .frequencyValue(6)
                .nextMaintenanceDate(LocalDate.now().plusMonths(6))
                .priority(MaintenancePriority.LOW)
                .active(true)
                .build();
        maintenancePlanRepository.save(plan2);

        // Search by planName contains
        mockMvc.perform(get("/api/v1/maintenance-plans/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("planName", "boiler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].planCode", is("MP-HEAT")));

        // Search by maintenanceType
        mockMvc.perform(get("/api/v1/maintenance-plans/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("maintenanceType", "CALIBRATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].planCode", is("MP-CALIB")));
    }

    @Test
    void whenCompleteMaintenancePlan_thenCalculatesNextRescheduledDate() throws Exception {
        MaintenancePlan plan = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-COMP")
                .planName("Complete Reschedule Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.MONTHLY)
                .frequencyValue(3)
                .nextMaintenanceDate(LocalDate.now())
                .priority(MaintenancePriority.LOW)
                .active(true)
                .build();
        plan = maintenancePlanRepository.save(plan);

        mockMvc.perform(post("/api/v1/maintenance-plans/" + plan.getId() + "/complete")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("completionDate", "2026-07-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastMaintenanceDate", is("2026-07-09")))
                .andExpect(jsonPath("$.nextMaintenanceDate", is("2026-10-09")));
    }

    @Test
    void whenUpdateMaintenancePlanWithInvalidStatusTransition_thenReturns400() throws Exception {
        MaintenancePlan plan = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-TRANS")
                .planName("State Transition Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now())
                .priority(MaintenancePriority.LOW)
                .status(MaintenanceStatus.CANCELLED)
                .active(true)
                .build();
        plan = maintenancePlanRepository.save(plan);

        MaintenancePlanUpdateRequest request = MaintenancePlanUpdateRequest.builder()
                .planName("State Transition Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now())
                .priority(MaintenancePriority.LOW)
                .status(MaintenanceStatus.IN_PROGRESS) // CANCELLED -> IN_PROGRESS is invalid!
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/maintenance-plans/" + plan.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSearchByStatus_thenReturnsFilteredResults() throws Exception {
        MaintenancePlan plan = MaintenancePlan.builder()
                .assetId(seededAsset.getId())
                .planCode("MP-STAT-1")
                .planName("Status Search Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.DAILY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now())
                .priority(MaintenancePriority.LOW)
                .status(MaintenanceStatus.IN_PROGRESS)
                .active(true)
                .build();
        maintenancePlanRepository.save(plan);

        mockMvc.perform(get("/api/v1/maintenance-plans/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].planCode", is("MP-STAT-1")));
    }
}
