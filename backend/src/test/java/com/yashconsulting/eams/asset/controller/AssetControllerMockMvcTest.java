package com.yashconsulting.eams.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.asset.dto.AssetCreateRequest;
import com.yashconsulting.eams.asset.dto.AssetResponse;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.service.AssetService;
import com.yashconsulting.eams.security.CustomUserDetailsService;
import com.yashconsulting.eams.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssetService assetService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void whenCreateAssetValid_thenReturns201() throws Exception {
        AssetCreateRequest request = AssetCreateRequest.builder()
                .assetCode("AST-101")
                .assetName("MacBook Pro")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(2500.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .assetCode("AST-101")
                .assetName("MacBook Pro")
                .build();

        when(assetService.createAsset(any(AssetCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.assetCode").value("AST-101"))
                .andExpect(jsonPath("$.assetName").value("MacBook Pro"));

        verify(assetService).createAsset(any(AssetCreateRequest.class));
    }

    @Test
    void whenCreateAssetInvalid_thenReturns400() throws Exception {
        AssetCreateRequest request = AssetCreateRequest.builder()
                .assetCode("") // blank code
                .assetName("   ") // blank name
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(-100.00)) // negative price
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(assetService, never()).createAsset(any());
    }


}
