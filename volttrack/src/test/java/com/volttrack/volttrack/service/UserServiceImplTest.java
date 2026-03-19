package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@volttrack.com")
                .password("encodedPass")
                .role(Role.CONSUMER)
                .active(true)
                .publicId("CON-1")
                .build();

        sampleRequest = UserRequestDto.builder()
                .username("testuser")
                .email("test@volttrack.com")
                .password("rawPass")
                .role("CONSUMER")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("createUser: Should correctly generate PublicID and save user")
    void testCreateUser_success() {
        when(userRepository.countByRole(Role.CONSUMER)).thenReturn(0L);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponseDto response = userService.createUser(sampleRequest);

        assertNotNull(response);
        assertEquals("CON-1", response.getPublicId());
        assertEquals("testuser", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getAllUsers: Should return paginated response")
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(sampleUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getUserById: Should return user when exists")
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponseDto response = userService.getUserById(1L);

        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("getUserById: Should throw exception when user not found")
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("getUserByPublicId: Should return user by publicId")
    void testGetUserByPublicId_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));

        UserResponseDto response = userService.getUserByPublicId("CON-1");

        assertEquals("CON-1", response.getPublicId());
    }

    @Test
    @DisplayName("deleteUserByPublicId: Should find and delete user")
    void testDeleteUserByPublicId_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));

        userService.deleteUserByPublicId("CON-1");

        verify(userRepository).delete(sampleUser);
    }

    @Test
    @DisplayName("deleteUser: Should delete user by ID if exists")
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: Should throw exception if user ID does not exist")
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("createConsumerActive: Should throw exception if email is taken")
    void testCreateConsumerActive_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createConsumerActive(sampleRequest));
    }

    @Test
    @DisplayName("createConsumerActive: Should save consumer with active=true")
    void testCreateConsumerActive_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.countByRole(Role.CONSUMER)).thenReturn(10L);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        // Mocking the saved user with the expected Public ID (10 + 1)
        User activeConsumer = sampleUser;
        activeConsumer.setPublicId("CON-11");
        activeConsumer.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(activeConsumer);

        UserResponseDto response = userService.createConsumerActive(sampleRequest);

        assertEquals("CON-11", response.getPublicId());
        assertTrue(response.getActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getConsumers: Should return only users with role CONSUMER")
    void testGetConsumers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> consumers = new PageImpl<>(Collections.singletonList(sampleUser));
        when(userRepository.findByRole(Role.CONSUMER, pageable)).thenReturn(consumers);

        Page<UserResponseDto> result = userService.getConsumers(pageable);

        assertFalse(result.isEmpty());
        verify(userRepository).findByRole(Role.CONSUMER, pageable);
    }

    @Test
    @DisplayName("approveOfficer: Should set active to true")
    void testApproveOfficer_Success() {
        User officer = User.builder()
                .publicId("OFF-5")
                .role(Role.OFFICER)
                .active(false)
                .build();

        when(userRepository.findByPublicId("OFF-5")).thenReturn(Optional.of(officer));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = userService.approveOfficer("OFF-5");

        assertTrue(result.getActive());
        verify(userRepository).save(officer);
    }
}