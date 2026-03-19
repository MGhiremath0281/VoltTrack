package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.security.CustomUserDetailsService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.MeterReadingService;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterReadingController.class)
public class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeterReadingService meterReadingService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // --- 1. Create Reading Test ---

    @Test
    @DisplayName("POST /api/meter-readings - Success with full hardware metrics")
    @WithMockUser(roles = "OFFICER")
    void createReading_Success() throws Exception {
        MeterReadingDTO requestDto = MeterReadingDTO.builder()
                .meterPublicId("MET-GRID-01")
                .pulseCount(1250)
                .voltage(230.5)
                .current(5.2)
                .unitsConsumed(12.45)
                .timestamp(LocalDateTime.now())
                .build();

        MeterReadingDTO responseDto = MeterReadingDTO.builder()
                .publicId("READ-999")
                .meterPublicId("MET-GRID-01")
                .pulseCount(1250)
                .voltage(230.5)
                .current(5.2)
                .unitsConsumed(12.45)
                .timestamp(requestDto.getTimestamp())
                .build();

        when(meterReadingService.saveReading(any(MeterReadingDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/meter-readings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("READ-999"))
                .andExpect(jsonPath("$.voltage").value(230.5))
                .andExpect(jsonPath("$.pulseCount").value(1250));
    }

    @Test
    @DisplayName("GET /api/meter-readings/{id} - Success")
    @WithMockUser(roles = "ADMIN")
    void getReadingByPublicId_Success() throws Exception {
        MeterReadingDTO responseDto = MeterReadingDTO.builder()
                .publicId("READ-999")
                .meterPublicId("MET-GRID-01")
                .build();

        when(meterReadingService.getReadingByPublicId("READ-999")).thenReturn(responseDto);

        mockMvc.perform(get("/api/meter-readings/READ-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("READ-999"));
    }

    @Test
    @DisplayName("GET /api/meter-readings - Paginated Success")
    @WithMockUser(roles = "OFFICER")
    void getAllReadings_Success() throws Exception {
        MeterReadingDTO dto = MeterReadingDTO.builder().publicId("READ-999").build();
        Page<MeterReadingDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(meterReadingService.getAllReadings(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/meter-readings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicId").value("READ-999"));
    }

    @Test
    @DisplayName("DELETE /api/meter-readings/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteReading_Success() throws Exception {
        doNothing().when(meterReadingService).deleteReadingByPublicId("READ-999");

        mockMvc.perform(delete("/api/meter-readings/READ-999")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/meter-readings/{id} - Forbidden as OFFICER")
    @WithMockUser(roles = "OFFICER")
    void deleteReading_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/meter-readings/READ-999")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/meter-readings - 400 Error on negative values")
    @WithMockUser(roles = "OFFICER")
    void createReading_NegativeValues_ReturnsBadRequest() throws Exception {
        MeterReadingDTO invalidDto = MeterReadingDTO.builder()
                .meterPublicId("MET-01")
                .voltage(-230.0) // Violates @Positive
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/meter-readings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}