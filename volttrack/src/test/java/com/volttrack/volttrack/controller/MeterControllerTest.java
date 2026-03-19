package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.security.CustomUserDetailsService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.MeterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterController.class)
public class MeterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeterService meterService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean(name = "meterSecurity")
    private Object meterSecurity;

    @Test
    @DisplayName("POST /api/meters - Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void createMeter_Success() throws Exception {
        MeterRequestDto request = MeterRequestDto.builder()
                .location("Kalaburagi Sector 2")
                .userPublicId("USER-777")
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();

        MeterResponseDto response = MeterResponseDto.builder()
                .meterId("MET-101")
                .status(Status.ONLINE)
                .build();

        when(meterService.createMeter(any(MeterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/meters")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meterId").value("MET-101"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    @DisplayName("GET /api/meters - Paginated Success")
    @WithMockUser(roles = "ADMIN")
    void getAllMeters_Success() throws Exception {
        MeterResponseDto dto = MeterResponseDto.builder().meterId("MET-101").build();
        Page<MeterResponseDto> page = new PageImpl<>(Collections.singletonList(dto));

        when(meterService.getAllMeters(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/meters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].meterId").value("MET-101"));
    }

    @Test
    @DisplayName("GET /api/meters/{publicId} - Success for OWNER")
    @WithMockUser(username = "consumer_user", roles = "USER")
    void getMeterByPublicId_SuccessForOwner() throws Exception {
        MeterResponseDto response = MeterResponseDto.builder()
                .meterId("MET-OWN-1")
                .build();

        when(meterService.getMeterByPublicId("MET-OWN-1")).thenReturn(response);

        mockMvc.perform(get("/api/meters/MET-OWN-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meterId").value("MET-OWN-1"));
    }

    @Test
    @DisplayName("DELETE /api/meters/{publicId} - Success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteMeter_Success() throws Exception {
        doNothing().when(meterService).deleteMeterByPublicId("MET-101");

        mockMvc.perform(delete("/api/meters/MET-101")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/meters/{publicId} - Forbidden for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void deleteMeter_ForbiddenForOfficer() throws Exception {
        mockMvc.perform(delete("/api/meters/MET-101")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/meters - 400 Bad Request on missing location")
    @WithMockUser(roles = "ADMIN")
    void createMeter_InvalidRequest_Returns400() throws Exception {
        MeterRequestDto invalidRequest = MeterRequestDto.builder()
                .userPublicId("USER-1")
                .build();

        mockMvc.perform(post("/api/meters")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}