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

        Role role = Role.valueOf(requestDto.getRole().toUpperCase());

        // Generate prefix based on role
        String prefix;
        switch (role) {
            case ADMIN -> prefix = "ADM";
            case OFFICER -> prefix = "OFF";
            case CONSUMER -> prefix = "CON";
            default -> prefix = "USR";
        }

        long count = userRepository.countByRole(role);
        String publicId = prefix + "-" + (count + 1);

        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(role)
                .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                .publicId(publicId)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with publicId={}", saved.getPublicId());

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

    public UserResponseDto getUserByPublicId(String publicId) {
        log.info("Fetching user with publicId={}", publicId);
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with publicId: " + publicId));
        return toResponseDto(user);
    }

    public void deleteUserByPublicId(String publicId) {
        log.info("Deleting user with publicId={}", publicId);
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with publicId: " + publicId));
        userRepository.delete(user);
        log.info("User deleted successfully with publicId={}", publicId);
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

        long count = userRepository.countByRole(Role.CONSUMER);
        String publicId = "CON-" + (count + 1);

        User consumer = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.CONSUMER)
                .active(true)
                .publicId(publicId)
                .build();

        User saved = userRepository.save(consumer);
        log.info("Consumer created successfully with publicId={}", saved.getPublicId());

        return toResponseDto(saved);
    }

    @Override
    public Page<UserResponseDto> getConsumers(Pageable pageable) {
        log.debug("Fetching all consumers with pagination");
        return userRepository.findByRole(Role.CONSUMER, pageable).map(this::toResponseDto);
    }

    @Override
    public UserResponseDto approveOfficer(String publicId) {
        log.info("Approving officer with publicId={}", publicId);

        User officer = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with publicId: " + publicId));

        officer.setActive(true);
        User saved = userRepository.save(officer);

        log.info("Officer approved successfully with publicId={}", publicId);
        return toResponseDto(saved);
    }


    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .build();
    }
}
