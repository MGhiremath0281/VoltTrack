package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.security.CustomUserDetailsService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.AlertService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertService alertService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/alerts - Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void createAlert_Success() throws Exception {
        AlertRequestDto request = AlertRequestDto.builder()
                .meterId(101L)
                .alertType("OVERLOAD")
                .message("High voltage detected at Substation A")
                .createdAt(LocalDateTime.now())
                .build();

        AlertResponseDto response = AlertResponseDto.builder()
                .id(1L)
                .message("High voltage detected at Substation A")
                .alertType("OVERLOAD")
                .meterId(101L)
                .build();

        when(alertService.createAlert(any(AlertRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/alerts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("High voltage detected at Substation A"))
                .andExpect(jsonPath("$.alertType").value("OVERLOAD"));
    }


    @Test
    @DisplayName("GET /api/alerts - Paginated Success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAllAlerts_Success() throws Exception {
        AlertResponseDto dto = AlertResponseDto.builder().id(1L).alertType("THEFT").build();
        Page<AlertResponseDto> page = new PageImpl<>(Collections.singletonList(dto));

        when(alertService.getAllAlerts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].alertType").value("THEFT"));
    }

    @Test
    @DisplayName("GET /api/alerts/{id} - Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void getAlertById_Success() throws Exception {
        AlertResponseDto response = AlertResponseDto.builder()
                .id(99L)
                .message("Connection Lost")
                .build();

        when(alertService.getAlertById(99L)).thenReturn(response);

        mockMvc.perform(get("/api/alerts/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    @DisplayName("DELETE /api/alerts/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteAlert_SuccessAsAdmin() throws Exception {
        doNothing().when(alertService).deleteAlert(anyLong());

        mockMvc.perform(delete("/api/alerts/99")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/alerts/{id} - Forbidden for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void deleteAlert_ForbiddenForOfficer() throws Exception {
        mockMvc.perform(delete("/api/alerts/99")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/alerts - 400 Bad Request if message is missing")
    @WithMockUser(roles = "OFFICER")
    void createAlert_ValidationError() throws Exception {
        AlertRequestDto invalidRequest = AlertRequestDto.builder()
                .meterId(101L)
                .build();

        mockMvc.perform(post("/api/alerts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}