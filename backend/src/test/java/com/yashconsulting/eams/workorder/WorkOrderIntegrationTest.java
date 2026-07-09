package com.yashconsulting.eams.workorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
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
class WorkOrderIntegrationTest extends BaseIntegrationTest {

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
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;

    private Asset seededAsset;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();

        // Seed users
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Eams")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        User manager = User.builder()
                .firstName("Manager")
                .lastName("Eams")
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
                .assetCode("AST-WO-101")
                .assetName("Machine Tool 101")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(15000.00))
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
    void whenAccessSecuredWorkOrderEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/work-orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateWorkOrderAsAdmin_thenReturns201() throws Exception {
        WorkOrderCreateRequest request = WorkOrderCreateRequest.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-999")
                .title("Urgent Pump Seal Replacement")
                .description("Pump seal is leaking heavily.")
                .assignedTechnician("Alice Technician")
                .priority(MaintenancePriority.CRITICAL)
                .status(WorkOrderStatus.REQUESTED)
                .scheduledDate(LocalDate.now().plusDays(1))
                .estimatedHours(BigDecimal.valueOf(2.5))
                .build();

        mockMvc.perform(post("/api/v1/work-orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.workOrderNumber", is("WO-999")))
                .andExpect(jsonPath("$.status", is("REQUESTED")));
    }

    @Test
    void whenCreateWorkOrderAsManager_thenReturns500InternalServerError() throws Exception {
        // Manager doesn't have permission to create work orders (requires ROLE_ADMIN)
        WorkOrderCreateRequest request = WorkOrderCreateRequest.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-100")
                .title("Minor Leak Fix")
                .priority(MaintenancePriority.LOW)
                .build();

        mockMvc.perform(post("/api/v1/work-orders")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateWorkOrderWithDuplicateNumber_thenReturns409() throws Exception {
        WorkOrder existing = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-DUP-1")
                .title("First WO")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
        workOrderRepository.save(existing);

        WorkOrderCreateRequest request = WorkOrderCreateRequest.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-DUP-1")
                .title("Second WO")
                .priority(MaintenancePriority.MEDIUM)
                .build();

        mockMvc.perform(post("/api/v1/work-orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void whenUpdateWorkOrder_thenReturns200() throws Exception {
        WorkOrder existing = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-UPDATE")
                .title("Old Title")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
        existing = workOrderRepository.save(existing);

        WorkOrderUpdateRequest request = WorkOrderUpdateRequest.builder()
                .title("New Title")
                .priority(MaintenancePriority.HIGH)
                .status(WorkOrderStatus.ASSIGNED) // REQUESTED -> ASSIGNED is valid!
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/work-orders/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Title")))
                .andExpect(jsonPath("$.status", is("ASSIGNED")));
    }

    @Test
    void whenUpdateWorkOrderWithInvalidTransition_thenReturns400() throws Exception {
        WorkOrder existing = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-INVALID-STATE")
                .title("Work Order State")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
        existing = workOrderRepository.save(existing);

        WorkOrderUpdateRequest request = WorkOrderUpdateRequest.builder()
                .title("Work Order State")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.COMPLETED) // REQUESTED -> COMPLETED is invalid!
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/work-orders/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid work order status transition")));
    }

    @Test
    void whenDeleteWorkOrder_thenSoftDeletesWorkOrder() throws Exception {
        WorkOrder existing = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-DELETE")
                .title("Temporary Work Order")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
        existing = workOrderRepository.save(existing);

        mockMvc.perform(delete("/api/v1/work-orders/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        WorkOrder deleted = workOrderRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchWorkOrders_thenFiltersCorrectly() throws Exception {
        WorkOrder wo1 = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-SEARCH-A")
                .title("Check Safety Valves")
                .assignedTechnician("Bob Smith")
                .priority(MaintenancePriority.HIGH)
                .status(WorkOrderStatus.REQUESTED)
                .active(true)
                .build();
        workOrderRepository.save(wo1);

        WorkOrder wo2 = WorkOrder.builder()
                .assetId(seededAsset.getId())
                .workOrderNumber("WO-SEARCH-B")
                .title("Realign Conveyor Belt")
                .assignedTechnician("Alice Johnson")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.ASSIGNED)
                .active(true)
                .build();
        workOrderRepository.save(wo2);

        // Search by title contains
        mockMvc.perform(get("/api/v1/work-orders/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("title", "conveyor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].workOrderNumber", is("WO-SEARCH-B")));

        // Search by technician contains
        mockMvc.perform(get("/api/v1/work-orders/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("assignedTechnician", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].workOrderNumber", is("WO-SEARCH-A")));
    }
}
