package com.volttrack.volttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.service.UserService;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.security.CustomUserDetailsService;
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

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/users - Success with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .username("volt_admin")
                .email("admin@volttrack.com")
                .password("securePass123")
                .role("ADMIN")
                .build();

        UserResponseDto response = UserResponseDto.builder()
                .publicId("ADM-101")
                .username("volt_admin")
                .role("ADMIN")
                .build();

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("ADM-101"));
    }

    @Test
    @DisplayName("GET /api/users - Paginated Success for OFFICER")
    @WithMockUser(roles = "OFFICER")
    void getAllUsers_Success() throws Exception {
        UserResponseDto userDto = UserResponseDto.builder().publicId("CON-501").username("consumer_1").build();
        Page<UserResponseDto> page = new PageImpl<>(Collections.singletonList(userDto));

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicId").value("CON-501"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Success for OFFICER (204 No Content)")
    @WithMockUser(roles = "OFFICER")
    void deleteUser_SuccessAsOfficer() throws Exception {

        doNothing().when(userService).deleteUser(any(Long.class));

        mockMvc.perform(delete("/api/users/CON-501")
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Matches your actual 204 response
    }


    @Test
    @DisplayName("GET /api/users - 401 Unauthorized without authentication")
    void requestWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users/{id} - 404 Not Found")
    @WithMockUser(roles = "ADMIN")
    void getUserByPublicId_NotFound() throws Exception {
        when(userService.getUserByPublicId("INVALID_ID"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/INVALID_ID"))
                .andExpect(status().isNotFound());
    }
}