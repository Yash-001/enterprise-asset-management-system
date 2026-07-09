package com.yashconsulting.eams.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.department.entity.Department;
import com.yashconsulting.eams.department.repository.DepartmentRepository;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.location.entity.Location;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.maintenance.entity.*;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ReportIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private com.yashconsulting.eams.inventory.repository.StockTransactionRepository stockTransactionRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        stockTransactionRepository.deleteAll();
        sparePartRepository.deleteAll();
        assetRepository.deleteAll();
        locationRepository.deleteAll();
        departmentRepository.deleteAll();
        vendorRepository.deleteAll();
        userRepository.deleteAll();

        // Seed users
        User user = User.builder()
                .firstName("Regular")
                .lastName("User")
                .email("user@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        userRepository.save(user);

        userToken = obtainAccessToken("user@eams.com", "User@123");

        // Seed references
        Department dept = departmentRepository.save(Department.builder()
                .departmentCode("DEPT-REP")
                .departmentName("Reporting Department")
                .active(true)
                .build());

        Location loc = locationRepository.save(Location.builder()
                .locationCode("LOC-REP")
                .locationName("Reporting Location")
                .active(true)
                .build());

        Vendor vendor = vendorRepository.save(Vendor.builder()
                .vendorCode("VEN-REP")
                .vendorName("Reporting Vendor")
                .active(true)
                .build());

        // Seed Assets
        Asset seededAsset = assetRepository.save(Asset.builder()
                .assetCode("ASSET-REP-1")
                .assetName("Reporting Asset 1")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1000.00))
                .status(AssetStatus.AVAILABLE)
                .departmentId(dept.getId())
                .locationId(loc.getId())
                .active(true)
                .build());

        assetRepository.save(Asset.builder()
                .assetCode("ASSET-REP-2")
                .assetName("Reporting Asset 2")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(2000.00))
                .status(AssetStatus.UNDER_MAINTENANCE)
                .departmentId(dept.getId())
                .locationId(loc.getId())
                .active(true)
                .build());

        // Seed Maintenance Plan
        maintenancePlanRepository.save(MaintenancePlan.builder()
                .planCode("MP-REP")
                .planName("Reporting MP")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyType(FrequencyType.MONTHLY)
                .frequencyValue(1)
                .nextMaintenanceDate(LocalDate.now().plusDays(10))
                .priority(MaintenancePriority.HIGH)
                .status(MaintenanceStatus.SCHEDULED)
                .assetId(seededAsset.getId())
                .active(true)
                .build());

        // Seed Work Orders
        workOrderRepository.save(WorkOrder.builder()
                .workOrderNumber("WO-REP-1")
                .assetId(seededAsset.getId())
                .title("WO 1")
                .priority(MaintenancePriority.MEDIUM)
                .status(WorkOrderStatus.IN_PROGRESS)
                .active(true)
                .build());

        workOrderRepository.save(WorkOrder.builder()
                .workOrderNumber("WO-REP-2")
                .assetId(seededAsset.getId())
                .title("WO 2")
                .priority(MaintenancePriority.LOW)
                .status(WorkOrderStatus.COMPLETED)
                .active(true)
                .build());

        // Seed Spare Parts
        sparePartRepository.save(SparePart.builder()
                .partNumber("PART-REP-1")
                .partName("Part 1")
                .unitCost(BigDecimal.valueOf(50.00))
                .currentStock(10)
                .minimumStock(15) // low stock
                .active(true)
                .build());

        sparePartRepository.save(SparePart.builder()
                .partNumber("PART-REP-2")
                .partName("Part 2")
                .unitCost(BigDecimal.valueOf(100.00))
                .currentStock(20)
                .minimumStock(5) // healthy stock
                .active(true)
                .build());

        // Seed Purchase Orders
        purchaseOrderRepository.save(PurchaseOrder.builder()
                .poNumber("PO-REP-1")
                .vendorId(vendor.getId())
                .status(PurchaseOrderStatus.RECEIVED)
                .orderDate(java.time.LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(500.00))
                .active(true)
                .build());
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
    void whenGetDashboardMetrics_thenReturnsConsolidatedReportPayload() throws Exception {
        mockMvc.perform(get("/api/v1/reports/dashboard")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetsByStatus.AVAILABLE", is(1)))
                .andExpect(jsonPath("$.assetsByStatus.UNDER_MAINTENANCE", is(1)))
                .andExpect(jsonPath("$.assetsByDepartment['Reporting Department']", is(2)))
                .andExpect(jsonPath("$.assetsByLocation['Reporting Location']", is(2)))
                .andExpect(jsonPath("$.upcomingMaintenanceCount", is(1)))
                .andExpect(jsonPath("$.completedWorkOrdersCount", is(1)))
                .andExpect(jsonPath("$.openWorkOrdersCount", is(1)))
                .andExpect(jsonPath("$.inventoryValuation", is(2500.0))) // (50 * 10) + (100 * 20)
                .andExpect(jsonPath("$.lowStockItemsCount", is(1)))
                .andExpect(jsonPath("$.purchaseOrdersByStatus.RECEIVED", is(1)))
                .andExpect(jsonPath("$.vendorPerformance", hasSize(1)))
                .andExpect(jsonPath("$.vendorPerformance[0].vendorCode", is("VEN-REP")))
                .andExpect(jsonPath("$.vendorPerformance[0].totalOrders", is(1)))
                .andExpect(jsonPath("$.vendorPerformance[0].totalSpend", is(500.0)));
    }
}
