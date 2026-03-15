package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;

public interface AuthService {
    UserResponseDto register(UserRequestDto requestDto);
    String login(String username, String password);
}
