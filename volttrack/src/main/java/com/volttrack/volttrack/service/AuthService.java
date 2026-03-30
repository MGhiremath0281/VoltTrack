package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.dto.user.AuthResponse;

public interface AuthService {
    UserResponseDto register(UserRequestDto requestDto);
    // Change String to AuthResponse here
    AuthResponse login(String username, String password);

}
