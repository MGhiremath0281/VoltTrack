package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_success() {

        UserRequestDto request = UserRequestDto.builder()
                .username("nitish")
                .email("nitish@test.com")
                .password("1234")
                .role("ADMIN")
                .active(true)
                .build();

        when(userRepository.countByRole(Role.ADMIN)).thenReturn(0L);

        when(passwordEncoder.encode("1234")).thenReturn("encodedPass");

        User savedUser = User.builder()
                .id(1L)
                .username("nitish")
                .email("nitish@test.com")
                .password("encodedPass")
                .role(Role.ADMIN)
                .active(true)
                .publicId("ADM-1")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.createUser(request);

        assertEquals("nitish", response.getUsername());
        assertEquals("ADM-1", response.getPublicId());
        assertEquals("ADMIN", response.getRole());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("1234");
    }
}