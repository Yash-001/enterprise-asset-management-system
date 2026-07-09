package com.yashconsulting.eams.vendor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.dto.VendorUpdateRequest;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.repository.StockTransactionRepository;
import com.yashconsulting.eams.location.repository.LocationRepository;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class VendorIntegrationTest extends BaseIntegrationTest {

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
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
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
    void whenAccessSecuredVendorEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/vendors"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateVendorAsAdmin_thenReturns201AndSaves() throws Exception {
        VendorCreateRequest request = VendorCreateRequest.builder()
                .vendorCode("VEN-VAL-1")
                .vendorName("Value Suppliers Ltd")
                .contactPerson("Alice Smith")
                .email("alice@valuesuppliers.com")
                .phone("1234567")
                .address("Industrial Plaza Block C")
                .city("Chicago")
                .state("Illinois")
                .country("USA")
                .postalCode("60601")
                .build();

        mockMvc.perform(post("/api/v1/vendors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.vendorCode", is("VEN-VAL-1")))
                .andExpect(jsonPath("$.vendorName", is("Value Suppliers Ltd")));
    }

    @Test
    void whenCreateVendorWithDuplicateCode_thenReturns409Conflict() throws Exception {
        Vendor existing = Vendor.builder()
                .vendorCode("VEN-DUP-X")
                .vendorName("Original Vendor")
                .active(true)
                .build();
        vendorRepository.save(existing);

        VendorCreateRequest request = VendorCreateRequest.builder()
                .vendorCode("ven-dup-x") // lower case should still trigger conflict
                .vendorName("Duplicate Vendor")
                .build();

        mockMvc.perform(post("/api/v1/vendors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void whenCreateVendorAsManager_thenReturns500InternalServerError() throws Exception {
        VendorCreateRequest request = VendorCreateRequest.builder()
                .vendorCode("VEN-MGR-1")
                .vendorName("Manager Request")
                .build();

        mockMvc.perform(post("/api/v1/vendors")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenUpdateVendor_thenSavesUpdates() throws Exception {
        Vendor existing = Vendor.builder()
                .vendorCode("VEN-UPD-1")
                .vendorName("Old Name")
                .active(true)
                .build();
        existing = vendorRepository.save(existing);

        VendorUpdateRequest request = VendorUpdateRequest.builder()
                .vendorName("New Name")
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/vendors/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendorName", is("New Name")));
    }

    @Test
    void whenDeleteVendor_thenSoftDeletesRecord() throws Exception {
        Vendor existing = Vendor.builder()
                .vendorCode("VEN-DEL-1")
                .vendorName("Temporary Vendor")
                .active(true)
                .build();
        existing = vendorRepository.save(existing);

        mockMvc.perform(delete("/api/v1/vendors/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Vendor deleted = vendorRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchVendors_thenFiltersCorrectly() throws Exception {
        Vendor v1 = Vendor.builder()
                .vendorCode("VEN-SRCH-A")
                .vendorName("Apex Parts")
                .contactPerson("David Miller")
                .city("Boston")
                .active(true)
                .build();
        vendorRepository.save(v1);

        Vendor v2 = Vendor.builder()
                .vendorCode("VEN-SRCH-B")
                .vendorName("Summit Supply")
                .contactPerson("Sophia Clark")
                .city("Dallas")
                .active(true)
                .build();
        vendorRepository.save(v2);

        // Search by name
        mockMvc.perform(get("/api/v1/vendors/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("vendorName", "apex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vendorCode", is("VEN-SRCH-A")));

        // Search by city
        mockMvc.perform(get("/api/v1/vendors/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("city", "dallas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vendorCode", is("VEN-SRCH-B")));
    }
}
