package com.yashconsulting.eams.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.repository.StockTransactionRepository;
import com.yashconsulting.eams.location.entity.Location;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderItemCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderItem;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PurchaseOrderIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;
    private String userToken;

    private Vendor seededVendor;
    private SparePart seededSparePart;
    private Location seededLocation;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        stockTransactionRepository.deleteAll();
        sparePartRepository.deleteAll();
        locationRepository.deleteAll();
        vendorRepository.deleteAll();
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

        // Seed vendor
        seededVendor = Vendor.builder()
                .vendorCode("VEN-PO-SEED")
                .vendorName("PO Seeder Corp")
                .active(true)
                .build();
        seededVendor = vendorRepository.save(seededVendor);

        // Seed Location
        seededLocation = Location.builder()
                .locationCode("LOC-PO-SHELF")
                .locationName("PO Storage Unit")
                .active(true)
                .build();
        seededLocation = locationRepository.save(seededLocation);

        // Seed spare part
        seededSparePart = SparePart.builder()
                .partNumber("SP-PO-SEED-PART")
                .partName("PO Integration Gear")
                .minimumStock(5)
                .maximumStock(100)
                .currentStock(10)
                .unitCost(BigDecimal.valueOf(12.00))
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
    void whenCreatePurchaseOrderAsManager_thenReturns201AndDRAFTStatus() throws Exception {
        PurchaseOrderCreateRequest request = PurchaseOrderCreateRequest.builder()
                .poNumber("PO-2026-X1")
                .vendorId(seededVendor.getId())
                .remarks("Initial order")
                .items(List.of(
                        PurchaseOrderItemCreateRequest.builder()
                                .sparePartId(seededSparePart.getId())
                                .quantity(15)
                                .unitPrice(BigDecimal.valueOf(15.00))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/purchase-orders")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.poNumber", is("PO-2026-X1")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.totalAmount", closeTo(225.00, 0.01)))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void whenTransitionPOStatusWorkflow_thenStatusUpdatesAndReachingRECEIVEDIncreasesStock() throws Exception {
        // 1. Create a PO (starts as DRAFT)
        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber("PO-WORKFLOW-STATUS")
                .vendorId(seededVendor.getId())
                .status(PurchaseOrderStatus.DRAFT)
                .orderDate(java.time.LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(100.00))
                .active(true)
                .build();
        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .sparePartId(seededSparePart.getId())
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(10.00))
                .lineTotal(BigDecimal.valueOf(100.00))
                .purchaseOrder(po)
                .build();
        po.getItems().add(item);
        final PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        // Verify initial stock is 10
        SparePart partBefore = sparePartRepository.findById(seededSparePart.getId()).orElseThrow();
        assertEquals(10, partBefore.getCurrentStock());

        // 2. DRAFT -> APPROVED
        PurchaseOrderUpdateRequest update1 = PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.APPROVED)
                .active(true)
                .items(List.of(
                        PurchaseOrderItemCreateRequest.builder()
                                .sparePartId(seededSparePart.getId())
                                .quantity(10)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/purchase-orders/" + savedPo.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        // 3. APPROVED -> ORDERED
        PurchaseOrderUpdateRequest update2 = PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.ORDERED)
                .active(true)
                .items(List.of(
                        PurchaseOrderItemCreateRequest.builder()
                                .sparePartId(seededSparePart.getId())
                                .quantity(10)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/purchase-orders/" + savedPo.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ORDERED")));

        // 4. ORDERED -> RECEIVED (should automatically increase inventory stock by 10)
        PurchaseOrderUpdateRequest update3 = PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.RECEIVED)
                .active(true)
                .items(List.of(
                        PurchaseOrderItemCreateRequest.builder()
                                .sparePartId(seededSparePart.getId())
                                .quantity(10)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/purchase-orders/" + savedPo.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RECEIVED")));

        // Verify stock has increased: 10 + 10 = 20
        SparePart partAfter = sparePartRepository.findById(seededSparePart.getId()).orElseThrow();
        assertEquals(20, partAfter.getCurrentStock());

        // Verify that a corresponding StockTransaction exists
        List<StockTransaction> transactions = stockTransactionRepository.findAll();
        boolean hasPoTransaction = transactions.stream()
                .anyMatch(t -> t.getTransactionType() == StockTransactionType.IN
                        && "PURCHASE_ORDER".equals(t.getReferenceType())
                        && savedPo.getId().equals(t.getReferenceId()));
        assertTrue(hasPoTransaction);
    }
}
