package com.yashconsulting.eams.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.dto.AssetUpdateRequest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AssetIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    void setUp() throws Exception {
        assetRepository.deleteAll();
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
    void whenAccessSecuredAssetEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAccessAdminAssetEndpointWithManagerToken_thenReturns500() throws Exception {
        AssetCreateRequest createRequest = AssetCreateRequest.builder()
                .assetCode("AST-001")
                .assetName("Developer Laptop")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1500.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateAssetWithAdminToken_thenReturns201AndSavesToDb() throws Exception {
        AssetCreateRequest createRequest = AssetCreateRequest.builder()
                .assetCode("AST-001")
                .assetName("Developer Laptop")
                .description("MacBook Pro M3")
                .serialNumber("SN-12345")
                .manufacturer("Apple")
                .model("M3 Pro")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(2500.50))
                .warrantyExpiry(LocalDate.now().plusYears(3))
                .status(AssetStatus.AVAILABLE)
                .departmentId(10L)
                .locationId(20L)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.assetCode", is("AST-001")))
                .andExpect(jsonPath("$.assetName", is("Developer Laptop")))
                .andExpect(jsonPath("$.description", is("MacBook Pro M3")))
                .andExpect(jsonPath("$.serialNumber", is("SN-12345")))
                .andExpect(jsonPath("$.manufacturer", is("Apple")))
                .andExpect(jsonPath("$.model", is("M3 Pro")))
                .andExpect(jsonPath("$.purchasePrice", is(2500.50)))
                .andExpect(jsonPath("$.status", is("AVAILABLE")))
                .andExpect(jsonPath("$.departmentId", is(10)))
                .andExpect(jsonPath("$.locationId", is(20)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void whenCreateAssetWithInvalidFields_thenReturns400() throws Exception {
        // Test blank fields and invalid whitespace
        AssetCreateRequest createRequest = AssetCreateRequest.builder()
                .assetCode(" AST-001 ")
                .assetName("Laptop")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1500.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must not contain leading or trailing spaces")));
    }

    @Test
    void whenCreateAssetWithDuplicateCode_thenReturns409() throws Exception {
        Asset existing = Asset.builder()
                .assetCode("AST-DUPLICATE")
                .assetName("Old Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        assetRepository.save(existing);

        AssetCreateRequest createRequest = AssetCreateRequest.builder()
                .assetCode("ast-duplicate") // lowercase should be normalized and detected as duplicate
                .assetName("New Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(200.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Asset code already exists")));
    }

    @Test
    void whenGetAssetById_thenReturnsAsset() throws Exception {
        Asset asset = Asset.builder()
                .assetCode("AST-GET")
                .assetName("Test Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        asset = assetRepository.save(asset);

        mockMvc.perform(get("/api/v1/assets/" + asset.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(asset.getId().intValue())))
                .andExpect(jsonPath("$.assetCode", is("AST-GET")))
                .andExpect(jsonPath("$.assetName", is("Test Asset")));
    }

    @Test
    void whenGetAssetByIdNotFound_thenReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/assets/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Asset not found")));
    }

    @Test
    void whenUpdateAsset_thenReturnsUpdatedAsset() throws Exception {
        Asset asset = Asset.builder()
                .assetCode("AST-UPDATE")
                .assetName("Old Name")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        asset = assetRepository.save(asset);

        AssetUpdateRequest updateRequest = AssetUpdateRequest.builder()
                .assetName("New Name")
                .description("Updated Description")
                .serialNumber("SN-NEW")
                .manufacturer("Dell")
                .model("Latitude")
                .purchaseDate(LocalDate.now().minusDays(1))
                .purchasePrice(BigDecimal.valueOf(120.50))
                .warrantyExpiry(LocalDate.now().plusYears(1))
                .status(AssetStatus.ASSIGNED)
                .departmentId(30L)
                .locationId(40L)
                .active(false)
                .build();

        mockMvc.perform(put("/api/v1/assets/" + asset.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName", is("New Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.serialNumber", is("SN-NEW")))
                .andExpect(jsonPath("$.manufacturer", is("Dell")))
                .andExpect(jsonPath("$.model", is("Latitude")))
                .andExpect(jsonPath("$.purchasePrice", is(120.50)))
                .andExpect(jsonPath("$.status", is("ASSIGNED")))
                .andExpect(jsonPath("$.departmentId", is(30)))
                .andExpect(jsonPath("$.locationId", is(40)))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    void whenDeleteAsset_thenSoftDeletesAsset() throws Exception {
        Asset asset = Asset.builder()
                .assetCode("AST-DELETE")
                .assetName("Delete Me")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(10.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        asset = assetRepository.save(asset);

        mockMvc.perform(delete("/api/v1/assets/" + asset.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Asset deleted = assetRepository.findById(asset.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchAssets_thenFiltersCorrectly() throws Exception {
        Asset asset1 = Asset.builder()
                .assetCode("AST-S1")
                .assetName("Office Chair")
                .serialNumber("SN-S1")
                .manufacturer("Herman Miller")
                .model("Aeron")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(1200.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        assetRepository.save(asset1);

        Asset asset2 = Asset.builder()
                .assetCode("AST-S2")
                .assetName("Developer MacBook")
                .serialNumber("SN-S2")
                .manufacturer("Apple")
                .model("MacBook Pro")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(3000.00))
                .status(AssetStatus.ASSIGNED)
                .active(true)
                .build();
        assetRepository.save(asset2);

        // Search by assetName ("MacBook")
        mockMvc.perform(get("/api/v1/assets/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("assetName", "MacBook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].assetCode", is("AST-S2")));

        // Search by manufacturer ("Herman")
        mockMvc.perform(get("/api/v1/assets/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("manufacturer", "Herman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].assetCode", is("AST-S1")));

        // Search by status ("ASSIGNED")
        mockMvc.perform(get("/api/v1/assets/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("status", "ASSIGNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].assetCode", is("AST-S2")));
    }

    @Test
    void whenUpdateAssetWithInvalidStatusTransition_thenReturns400() throws Exception {
        Asset asset = Asset.builder()
                .assetCode("AST-TRANS")
                .assetName("Disposed Computer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.DISPOSED)
                .active(true)
                .build();
        asset = assetRepository.save(asset);

        AssetUpdateRequest updateRequest = AssetUpdateRequest.builder()
                .assetName("Disposed Computer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/assets/" + asset.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid asset status transition")));
    }
}
