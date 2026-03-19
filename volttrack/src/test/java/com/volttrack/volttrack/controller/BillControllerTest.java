package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.security.CustomUserDetailsService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.BillService;
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

@WebMvcTest(BillController.class)
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BillService billService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("POST /api/bills - Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void createBill_Success() throws Exception {
        BillRequestDto request = BillRequestDto.builder()
                .meterPublicId("MET-GRID-101")
                .build();

        BillResponseDto response = BillResponseDto.builder()
                .publicId("BILL-2026-XYZ")
                .meterPublicId("MET-GRID-101")
                .consumerPublicId("CON-777")
                .billingCycle("MONTHLY")
                .totalAmount(1250.50)
                .status("PENDING")
                .generatedAt(LocalDateTime.now())
                .build();

        when(billService.createBill(any(BillRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/bills")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("BILL-2026-XYZ"))
                .andExpect(jsonPath("$.totalAmount").value(1250.50))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.billingCycle").value("MONTHLY"));
    }

    @Test
    @DisplayName("GET /api/bills - Paginated List for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAllBills_Success() throws Exception {
        BillResponseDto dto = BillResponseDto.builder()
                .publicId("BILL-001")
                .totalAmount(500.0)
                .build();
        Page<BillResponseDto> page = new PageImpl<>(Collections.singletonList(dto));

        when(billService.getAllBills(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/bills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicId").value("BILL-001"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(500.0));
    }

    @Test
    @DisplayName("GET /api/bills/{id} - Single Retrieval for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void getBillByPublicId_Success() throws Exception {
        BillResponseDto response = BillResponseDto.builder()
                .publicId("BILL-001")
                .unitsConsumed(100.0)
                .taxAmount(25.5)
                .build();

        when(billService.getBillByPublicId("BILL-001")).thenReturn(response);

        mockMvc.perform(get("/api/bills/BILL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("BILL-001"))
                .andExpect(jsonPath("$.taxAmount").value(25.5));
    }

    @Test
    @DisplayName("DELETE /api/bills/{id} - Success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteBill_SuccessAsAdmin() throws Exception {
        doNothing().when(billService).deleteBillByPublicId("BILL-001");

        mockMvc.perform(delete("/api/bills/BILL-001")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/bills/{id} - Forbidden for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void deleteBill_ForbiddenForOfficer() throws Exception {
        mockMvc.perform(delete("/api/bills/BILL-001")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/bills - Forbidden for ROLE_USER")
    @WithMockUser(roles = "USER")
    void getAllBills_ForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/bills"))
                .andExpect(status().isForbidden());
    }
}