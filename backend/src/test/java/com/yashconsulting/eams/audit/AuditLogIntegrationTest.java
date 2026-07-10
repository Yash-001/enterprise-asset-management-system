package com.yashconsulting.eams.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.audit.entity.AuditLog;
import com.yashconsulting.eams.audit.repository.AuditLogRepository;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuditLogIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        auditLogRepository.deleteAll();
        assetRepository.deleteAll();
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

        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
    }

    @Test
    void createAsset_generatesAuditLog() throws Exception {
        AssetCreateRequest request = AssetCreateRequest.builder()
                .assetCode("AST-AUDIT-001")
                .assetName("Audited Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(500.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Audit logging is async — wait for it
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc("Asset", null);
            // Fallback: just check any Asset CREATE log exists
            List<AuditLog> allLogs = auditLogRepository.findAll();
            boolean found = allLogs.stream()
                    .anyMatch(log -> "Asset".equals(log.getEntityName()) && log.getAction() == AuditAction.CREATE);
            assertTrue(found, "Expected an Asset CREATE audit log");
        });
    }

    @Test
    void searchAuditLogs_asAdmin_returnsResults() throws Exception {
        // Create an audit log entry directly
        AuditLog log = AuditLog.builder()
                .entityName("Asset")
                .action(AuditAction.CREATE)
                .entityId(1L)
                .afterValue("{\"id\":1}")
                .performedBy("admin@eams.com")
                .performedAt(java.time.LocalDateTime.now())
                .ipAddress("127.0.0.1")
                .build();
        auditLogRepository.save(log);

        mockMvc.perform(get("/api/v1/audit-logs")
                        .param("entityName", "Asset")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].entityName", is("Asset")))
                .andExpect(jsonPath("$.content[0].action", is("CREATE")));
    }

    @Test
    void getEntityHistory_returnsFullTrail() throws Exception {
        AuditLog log1 = AuditLog.builder()
                .entityName("WorkOrder").action(AuditAction.CREATE).entityId(5L)
                .performedBy("admin@eams.com").performedAt(java.time.LocalDateTime.now().minusHours(2))
                .build();
        AuditLog log2 = AuditLog.builder()
                .entityName("WorkOrder").action(AuditAction.UPDATE).entityId(5L)
                .performedBy("admin@eams.com").performedAt(java.time.LocalDateTime.now())
                .build();
        auditLogRepository.saveAll(List.of(log1, log2));

        mockMvc.perform(get("/api/v1/audit-logs/entity/WorkOrder/5")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].action", is("UPDATE")))
                .andExpect(jsonPath("$[1].action", is("CREATE")));
    }

    @Test
    void searchAuditLogs_withoutAdminRole_returnsForbidden() throws Exception {
        User regularUser = User.builder()
                .firstName("Regular")
                .lastName("User")
                .email("user@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(regularUser);

        String userToken = obtainAccessToken("user@eams.com", "User@123");

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class).getAccessToken();
    }
}
