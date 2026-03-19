package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.security.CustomUserDetailsService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.AlertService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficerDashboardController.class)
public class OfficerDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private MeterService meterService;
    @MockBean private BillService billService;
    @MockBean private AlertService alertService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /alerts/{id} - Success with AlertRequestDto")
    @WithMockUser(roles = "OFFICER")
    void createAlert_Success() throws Exception {
        AlertRequestDto request = AlertRequestDto.builder()
                .meterId(101L)
                .alertType("OVERLOAD")
                .message("Voltage Drop detected")
                .createdAt(LocalDateTime.now())
                .publicId("ALT-XYZ")
                .build();

        AlertResponseDto response = AlertResponseDto.builder()
                .message("Voltage Drop detected")
                .alertType("OVERLOAD")
                .meterId(101L)
                .build();

        when(alertService.createAlertForConsumer(anyString(), any(AlertRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/officer/dashboard/alerts/CON-1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Voltage Drop detected"))
                .andExpect(jsonPath("$.alertType").value("OVERLOAD"));
    }


    @Test
    @DisplayName("POST /meters/assign/{id} - Success with MeterRequestDto")
    @WithMockUser(roles = "OFFICER")
    void assignMeter_Success() throws Exception {
        MeterRequestDto request = MeterRequestDto.builder()
                .location("Kalaburagi Main Substation")
                .userPublicId("CON-123")
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();

        MeterResponseDto response = MeterResponseDto.builder()
                .meterId("MET-8899")
                .status(Status.ONLINE)
                .build();

        when(meterService.assignMeterToConsumer(anyString(), any(MeterRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/officer/dashboard/meters/assign/CON-123")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meterId").value("MET-8899"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }


    @Test
    @DisplayName("POST /consumers - Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void createConsumer_Success() throws Exception {
        UserRequestDto request = UserRequestDto.builder().username("nitish").email("nitish@volt.com").build();
        UserResponseDto response = UserResponseDto.builder().publicId("CON-1").username("nitish").build();

        when(userService.createConsumerActive(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/officer/dashboard/consumers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("CON-1"));
    }

    @Test
    @DisplayName("GET /consumers - Paginated Success")
    @WithMockUser(roles = "OFFICER")
    void getConsumers_Success() throws Exception {
        UserResponseDto dto = UserResponseDto.builder().publicId("CON-1").build();
        Page<UserResponseDto> page = new PageImpl<>(Collections.singletonList(dto));

        when(userService.getConsumers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/officer/dashboard/consumers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicId").value("CON-1"));
    }


    @Test
    @DisplayName("POST /bills/{id} - Success")
    @WithMockUser(roles = "OFFICER")
    void generateBill_Success() throws Exception {
        BillResponseDto response = BillResponseDto.builder()
                .publicId("B-99")
                .totalAmount(250.50)
                .build();

        when(billService.generateBillForConsumer(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/officer/dashboard/bills/CON-1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("B-99"));
    }
}