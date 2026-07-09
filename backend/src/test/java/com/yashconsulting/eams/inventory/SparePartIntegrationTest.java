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
import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
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
class SparePartIntegrationTest extends BaseIntegrationTest {

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
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;

    private Location seededLocation;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
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

        // Fetch tokens
        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        managerToken = obtainAccessToken("manager@eams.com", "Manager@123");

        // Seed location
        seededLocation = Location.builder()
                .locationCode("LOC-INV-SHELF1")
                .locationName("Inventory Shelf 1")
                .active(true)
                .build();
        seededLocation = locationRepository.save(seededLocation);
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
    void whenAccessSecuredSparePartEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/spare-parts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateSparePartAsAdmin_thenReturns201() throws Exception {
        SparePartCreateRequest request = SparePartCreateRequest.builder()
                .partNumber("SP-INV-COG")
                .partName("Optibelt Drive Cog")
                .description("Heavy duty industrial cog")
                .manufacturer("Optibelt")
                .category("Cogs & Pulleys")
                .unitOfMeasure("PCS")
                .minimumStock(5)
                .maximumStock(100)
                .currentStock(20)
                .unitCost(BigDecimal.valueOf(18.75))
                .locationId(seededLocation.getId())
                .build();

        mockMvc.perform(post("/api/v1/spare-parts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.partNumber", is("SP-INV-COG")))
                .andExpect(jsonPath("$.locationId", is(seededLocation.getId().intValue())));
    }

    @Test
    void whenCreateSparePartWithDuplicateNumber_thenReturns409() throws Exception {
        SparePart existing = SparePart.builder()
                .partNumber("SP-INV-DUP")
                .partName("First Part")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .active(true)
                .build();
        sparePartRepository.save(existing);

        SparePartCreateRequest request = SparePartCreateRequest.builder()
                .partNumber("SP-INV-DUP")
                .partName("Second Part")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .build();

        mockMvc.perform(post("/api/v1/spare-parts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void whenUpdateSparePart_thenReturns200() throws Exception {
        SparePart existing = SparePart.builder()
                .partNumber("SP-INV-UPDATE")
                .partName("Old Name")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .active(true)
                .build();
        existing = sparePartRepository.save(existing);

        SparePartUpdateRequest request = SparePartUpdateRequest.builder()
                .partName("New Name")
                .minimumStock(5)
                .maximumStock(50)
                .currentStock(15)
                .unitCost(BigDecimal.TEN)
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/spare-parts/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partName", is("New Name")))
                .andExpect(jsonPath("$.currentStock", is(15)));
    }

    @Test
    void whenDeleteSparePart_thenSoftDeletesPart() throws Exception {
        SparePart existing = SparePart.builder()
                .partNumber("SP-INV-DELETE")
                .partName("Temporary Part")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .active(true)
                .build();
        existing = sparePartRepository.save(existing);

        mockMvc.perform(delete("/api/v1/spare-parts/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        SparePart deleted = sparePartRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchSpareParts_thenFiltersCorrectly() throws Exception {
        SparePart p1 = SparePart.builder()
                .partNumber("SP-SRCH-A")
                .partName("Safety Goggles")
                .category("Safety")
                .manufacturer("3M")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .active(true)
                .build();
        sparePartRepository.save(p1);

        SparePart p2 = SparePart.builder()
                .partNumber("SP-SRCH-B")
                .partName("Welding Helmet")
                .category("Welding")
                .manufacturer("Lincoln Electric")
                .minimumStock(0)
                .maximumStock(10)
                .currentStock(1)
                .unitCost(BigDecimal.ONE)
                .active(true)
                .build();
        sparePartRepository.save(p2);

        // Search by category
        mockMvc.perform(get("/api/v1/spare-parts/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("category", "safety"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].partNumber", is("SP-SRCH-A")));

        // Search by manufacturer
        mockMvc.perform(get("/api/v1/spare-parts/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("manufacturer", "lincoln"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].partNumber", is("SP-SRCH-B")));
    }
}
