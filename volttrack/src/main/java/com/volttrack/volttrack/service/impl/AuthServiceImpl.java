package com.volttrack.volttrack.service.impl;

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
import org.springframework.cache.annotation.Cacheable;
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

        // Convert string role from DTO to enum
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(requestDto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided: " + requestDto.getRole());
        }

        if (isEmailExists(requestDto.getEmail())) {
            throw new RuntimeException("Email already registered: " + requestDto.getEmail());
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

        // Build DTO using the actual enum
        return UserResponseDto.builder()
                .id(saved.getId())
                .publicId(saved.getPublicId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole())   // ✅ use enum, not String
                .active(saved.getActive())
                .build();
    }

    @Override
    @Cacheable(value = "userTokens", key = "#username")
    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = userRepository.findByUsername(username)
                    .map(user -> org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRole().name()) // Spring expects ROLE_ prefix automatically
                            .build()
                    ).orElseThrow(() -> new RuntimeException("User not found: " + username));

            return jwtUtil.generateToken(userDetails);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Cacheable(value = "usersByEmail", key = "#email")
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private String generatePublicId(Role role) {
        String prefix = switch (role) {
            case ADMIN -> "ADM";
            case OFFICER -> "OFF";
            case CONSUMER -> "CON";
        };
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}