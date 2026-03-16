package com.volttrack.volttrack.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.Role;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        log.info("Creating new user with username={} and email={}", requestDto.getUsername(), requestDto.getEmail());

        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.valueOf(requestDto.getRole().toUpperCase()))
                .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with id={}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination");
        return userRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponseDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id={}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id={}", id);
    }

    @Override
    public UserResponseDto createConsumerActive(UserRequestDto requestDto) {
        log.info("Creating consumer with email={}", requestDto.getEmail());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + requestDto.getEmail());
        }

        User consumer = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.CONSUMER)
                .active(true)
                .build();

        User saved = userRepository.save(consumer);
        log.info("Consumer created successfully with id={}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    public Page<UserResponseDto> getConsumers(Pageable pageable) {
        log.debug("Fetching all consumers with pagination");
        return userRepository.findByRole(Role.CONSUMER, pageable).map(this::toResponseDto);
    }

    // 🔹 New method for approving officers
    @Override
    public UserResponseDto approveOfficer(Long id) {
        log.info("Approving officer with id={}", id);

        User officer = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + id));

        if (!officer.getRole().equals(Role.OFFICER)) {
            throw new IllegalStateException("Only officers can be approved");
        }

        officer.setActive(true);
        User saved = userRepository.save(officer);

        log.info("Officer approved successfully with id={}", saved.getId());
        return toResponseDto(saved);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .build();
    }
}
