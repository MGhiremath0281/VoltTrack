package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.user.AuthResponse;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user (Consumer or Officer)")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(authService.register(requestDto));
    }

    @Operation(summary = "Login and receive JWT with Role")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "403", description = "Account pending approval")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto requestDto) {
        try {
            // All authentication logic, role extraction, and
            // approval checks are now handled inside authService.login
            AuthResponse response = authService.login(
                    requestDto.getUsername(),
                    requestDto.getPassword()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle specific business logic errors (like 'Account not approved')
            if (e.getMessage().contains("approved")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }

            // Handle bad credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}