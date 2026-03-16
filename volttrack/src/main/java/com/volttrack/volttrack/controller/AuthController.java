package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserRequestDto requestDto) {
        String token = authService.login(requestDto.getUsername(), requestDto.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

}
