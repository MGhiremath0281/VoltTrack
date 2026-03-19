package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private MeterService meterService;
    @MockBean private BillService billService;
    @MockBean private AlertService alertService;

    @MockBean private JwtUtil jwtUtil;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/admin/dashboard/users - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        UserResponseDto user = UserResponseDto.builder().publicId("USR-100").username("muktananda").build();
        Page<UserResponseDto> page = new PageImpl<>(Collections.singletonList(user));

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/dashboard/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicId").value("USR-100"));
    }

    @Test
    @DisplayName("PUT /api/admin/dashboard/users/{id}/approve - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void approveOfficer_Success() throws Exception {
        UserResponseDto approvedUser = UserResponseDto.builder().publicId("OFF-1").username("officer_john").build();

        when(userService.approveOfficer("OFF-1")).thenReturn(approvedUser);

        mockMvc.perform(put("/api/admin/dashboard/users/OFF-1/approve")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("OFF-1"));
    }

    @Test
    @DisplayName("DELETE /api/admin/dashboard/meters/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteMeter_Success() throws Exception {
        doNothing().when(meterService).deleteMeterByPublicId("MET-99");

        mockMvc.perform(delete("/api/admin/dashboard/meters/MET-99")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/admin/dashboard/bills/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getBillByPublicId_Success() throws Exception {
        BillResponseDto bill = BillResponseDto.builder().publicId("BILL-001").totalAmount(500.0).build();

        when(billService.getBillByPublicId("BILL-001")).thenReturn(bill);

        mockMvc.perform(get("/api/admin/dashboard/bills/BILL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("BILL-001"));
    }

    @Test
    @DisplayName("DELETE /api/admin/dashboard/alerts/{id} - Success as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteAlert_Success() throws Exception {
        doNothing().when(alertService).deleteAlertByPublicId("ALT-55");

        mockMvc.perform(delete("/api/admin/dashboard/alerts/ALT-55")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Any Admin Dashboard endpoint - Forbidden for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void adminDashboard_ForbiddenForOfficer() throws Exception {
        // Testing one endpoint as a representative for the whole class
        mockMvc.perform(get("/api/admin/dashboard/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Any Admin Dashboard endpoint - Forbidden for USER")
    @WithMockUser(roles = "USER")
    void adminDashboard_ForbiddenForUser() throws Exception {
        mockMvc.perform(delete("/api/admin/dashboard/bills/BILL-01")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}