package com.volttrack.volttrack.service.impl;

import java.util.List;
import java.util.stream.Collectors;

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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        log.info("Creating new user with username={} and email={}", requestDto.getUsername(), requestDto.getEmail());

        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .role(Role.valueOf(requestDto.getRole().toUpperCase()))
                .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with id={}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.debug("Fetching all users from repository");
        return userRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        log.info("User found with id={}", id);
        return toResponseDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id={}", id);
        if (!userRepository.existsById(id)) {
            log.error("Cannot delete. User not found with id={}", id);
            throw new ResourceNotFoundException("Cannot delete. User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id={}", id);
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
