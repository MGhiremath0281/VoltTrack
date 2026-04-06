package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.user.AuthResponse;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.security.JwtUtil;
import com.volttrack.volttrack.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @CacheEvict(value = "usersByEmail", key = "#requestDto.email")
    public UserResponseDto register(UserRequestDto requestDto) {
        // 1. Role Conversion
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(requestDto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided: " + requestDto.getRole());
        }

        // 2. Check for duplicate Email AND Username
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + requestDto.getEmail());
        }
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken: " + requestDto.getUsername());
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(roleEnum)
                .active(true)
                .publicId(generatePublicId(roleEnum))
                .build();

        User saved = userRepository.save(user);

        return UserResponseDto.builder()
                .id(saved.getId())
                .publicId(saved.getPublicId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole())
                .active(saved.getActive())
                .build();
    }

    @Override
    public AuthResponse login(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            return userRepository.findByUsername(username)
                    .map(user -> {
                        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles(user.getRole().name()) // automatically adds ROLE_
                                .build();

                        String token = jwtUtil.generateToken(userDetails);

                        return new AuthResponse(token, user.getRole().name());
                    })
                    .orElseThrow(() -> new RuntimeException("User not found after auth: " + username));

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    private String generatePublicId(Role role) {
        String prefix = switch (role) {
            case ADMIN -> "ADM";
            case OFFICER -> "OFF";
            case CONSUMER -> "CON";
            case SUB_DISTRICT_OFFICER -> "SDO";
            default -> "USR"; // fallback for future roles
        };
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
