package com.yashconsulting.eams.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.location.entity.Location;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionUpdateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.repository.StockTransactionRepository;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class StockTransactionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;
    private String userToken;

    private SparePart seededSparePart;
    private Location seededLocation;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
        stockTransactionRepository.deleteAll();
        sparePartRepository.deleteAll();
        locationRepository.deleteAll();
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

        User user = User.builder()
                .firstName("User")
                .lastName("Eams")
                .email("user@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(user);

        // Fetch tokens
        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        managerToken = obtainAccessToken("manager@eams.com", "Manager@123");
        userToken = obtainAccessToken("user@eams.com", "User@123");

        // Seed location
        seededLocation = Location.builder()
                .locationCode("LOC-TRANS-1")
                .locationName("Storage Locker A")
                .active(true)
                .build();
        seededLocation = locationRepository.save(seededLocation);

        // Seed spare part
        seededSparePart = SparePart.builder()
                .partNumber("SP-TRANS-SKU")
                .partName("Test Gear Part")
                .minimumStock(5)
                .maximumStock(100)
                .currentStock(20)
                .unitCost(BigDecimal.valueOf(10.00))
                .locationId(seededLocation.getId())
                .active(true)
                .build();
        seededSparePart = sparePartRepository.save(seededSparePart);
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
    void whenAccessSecuredTransactionEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/stock-transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateStockTransactionAsManager_thenReturns201AndIncrementsStock() throws Exception {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.IN)
                .quantity(15)
                .unitCost(BigDecimal.valueOf(12.50))
                .referenceType("PURCHASE_ORDER")
                .referenceId(101L)
                .remarks("Incoming supplier order")
                .build();

        mockMvc.perform(post("/api/v1/stock-transactions")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.quantity", is(15)))
                .andExpect(jsonPath("$.transactionType", is("IN")));

        SparePart updated = sparePartRepository.findById(seededSparePart.getId()).orElseThrow();
        assertEquals(35, updated.getCurrentStock()); // 20 + 15 = 35
    }

    @Test
    void whenCreateStockTransactionAsUser_thenReturns500InternalServerError() throws Exception {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.IN)
                .quantity(5)
                .unitCost(BigDecimal.ONE)
                .build();

        mockMvc.perform(post("/api/v1/stock-transactions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateOUTTransactionExceedsStock_thenReturns400() throws Exception {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.OUT)
                .quantity(30) // Exceeds currentStock=20
                .unitCost(BigDecimal.valueOf(10.00))
                .build();

        mockMvc.perform(post("/api/v1/stock-transactions")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Insufficient stock level")));

        SparePart updated = sparePartRepository.findById(seededSparePart.getId()).orElseThrow();
        assertEquals(20, updated.getCurrentStock()); // Unchanged
    }

    @Test
    void whenDeleteStockTransaction_thenSoftDeletesAndRevertsStock() throws Exception {
        // First record a stock transaction: OUT of 5 items (stock becomes 20 - 5 = 15)
        StockTransaction transaction = StockTransaction.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.OUT)
                .quantity(5)
                .unitCost(BigDecimal.valueOf(10.00))
                .performedBy("admin@eams.com")
                .active(true)
                .build();
        transaction = stockTransactionRepository.save(transaction);
        seededSparePart.setCurrentStock(15);
        sparePartRepository.save(seededSparePart);

        mockMvc.perform(delete("/api/v1/stock-transactions/" + transaction.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        StockTransaction deleted = stockTransactionRepository.findById(transaction.getId()).orElseThrow();
        assertFalse(deleted.getActive());

        SparePart updated = sparePartRepository.findById(seededSparePart.getId()).orElseThrow();
        assertEquals(20, updated.getCurrentStock()); // Reverted (15 + 5 = 20)
    }

    @Test
    void whenSearchStockTransactions_thenFiltersCorrectly() throws Exception {
        // Create t1 as manager
        StockTransactionCreateRequest r1 = StockTransactionCreateRequest.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.IN)
                .quantity(10)
                .unitCost(BigDecimal.TEN)
                .referenceType("PURCHASE_ORDER")
                .build();
        mockMvc.perform(post("/api/v1/stock-transactions")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r1)))
                .andExpect(status().isCreated());

        // Create t2 as admin
        StockTransactionCreateRequest r2 = StockTransactionCreateRequest.builder()
                .sparePartId(seededSparePart.getId())
                .transactionType(StockTransactionType.OUT)
                .quantity(5)
                .unitCost(BigDecimal.TEN)
                .referenceType("WORK_ORDER")
                .build();
        mockMvc.perform(post("/api/v1/stock-transactions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r2)))
                .andExpect(status().isCreated());

        // Search by referenceType
        mockMvc.perform(get("/api/v1/stock-transactions/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("referenceType", "purchase"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].transactionType", is("IN")));

        // Search by performedBy
        mockMvc.perform(get("/api/v1/stock-transactions/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("performedBy", "admin@eams.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].transactionType", is("OUT")));
    }
}
