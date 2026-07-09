package com.yashconsulting.eams.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.location.dto.LocationCreateRequest;
import com.yashconsulting.eams.location.dto.LocationUpdateRequest;
import com.yashconsulting.eams.location.entity.Location;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.user.repository.UserRepository;
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
class LocationIntegrationTest extends BaseIntegrationTest {

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
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    void setUp() throws Exception {
        sparePartRepository.deleteAll();
        locationRepository.deleteAll();
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
    void whenAccessSecuredLocationEndpointsWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenCreateLocationAsAdmin_thenReturns201() throws Exception {
        LocationCreateRequest request = LocationCreateRequest.builder()
                .locationCode("LOC-1")
                .locationName("Main Office")
                .building("Tower A")
                .floor("Floor 2")
                .room("Room 201")
                .description("HQ Office")
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.locationCode", is("LOC-1")))
                .andExpect(jsonPath("$.locationName", is("Main Office")))
                .andExpect(jsonPath("$.building", is("Tower A")))
                .andExpect(jsonPath("$.floor", is("Floor 2")))
                .andExpect(jsonPath("$.room", is("Room 201")));
    }

    @Test
    void whenCreateLocationAsManager_thenReturns500() throws Exception {
        LocationCreateRequest request = LocationCreateRequest.builder()
                .locationCode("LOC-2")
                .locationName("Branch Office")
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateLocationWithDuplicateCode_thenReturns409() throws Exception {
        Location existing = Location.builder()
                .locationCode("LOC-DUP")
                .locationName("Original Loc")
                .active(true)
                .build();
        locationRepository.save(existing);

        LocationCreateRequest request = LocationCreateRequest.builder()
                .locationCode("LOC-DUP")
                .locationName("Duplicate Loc")
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("is already registered")));
    }

    @Test
    void whenCreateLocationWithWhitespace_thenTrimmed() throws Exception {
        LocationCreateRequest request = LocationCreateRequest.builder()
                .locationCode("LOC-SPACE")
                .locationName("Clean Name")
                .building("Clean Building")
                .description("   Clean Desc   ")
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Clean Desc")));
    }

    @Test
    void whenCreateLocationWithBlankRequiredFields_thenReturns400() throws Exception {
        LocationCreateRequest request = LocationCreateRequest.builder()
                .locationCode("   ")
                .locationName("")
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateLocation_thenReturns200() throws Exception {
        Location existing = Location.builder()
                .locationCode("LOC-TO-UPDATE")
                .locationName("Old Name")
                .active(true)
                .build();
        existing = locationRepository.save(existing);

        LocationUpdateRequest request = LocationUpdateRequest.builder()
                .locationName("New Name")
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/locations/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationName", is("New Name")))
                .andExpect(jsonPath("$.locationCode", is("LOC-TO-UPDATE")));
    }

    @Test
    void whenDeleteLocation_thenSoftDeletes() throws Exception {
        Location existing = Location.builder()
                .locationCode("LOC-TO-DELETE")
                .locationName("Delete Me")
                .active(true)
                .build();
        existing = locationRepository.save(existing);

        mockMvc.perform(delete("/api/v1/locations/" + existing.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Location deleted = locationRepository.findById(existing.getId()).orElseThrow();
        assertFalse(deleted.getActive());
    }

    @Test
    void whenSearchLocations_thenFiltersCorrectly() throws Exception {
        Location loc1 = Location.builder()
                .locationCode("LOC-A")
                .locationName("Office Alpha")
                .building("Building A")
                .active(true)
                .build();
        locationRepository.save(loc1);

        Location loc2 = Location.builder()
                .locationCode("LOC-B")
                .locationName("Office Beta")
                .building("Building B")
                .active(true)
                .build();
        locationRepository.save(loc2);

        // Search by locationName
        mockMvc.perform(get("/api/v1/locations/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("locationName", "Alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].locationCode", is("LOC-A")));

        // Search by building
        mockMvc.perform(get("/api/v1/locations/search")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("building", "building b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].locationCode", is("LOC-B")));
    }
}
