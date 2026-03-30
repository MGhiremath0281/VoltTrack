package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRepository meterRepository; // Added for refresh-proof logic

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MeterRepository meterRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRepository = meterRepository;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        log.info("Creating new user: {}", requestDto.getUsername());

        Role role = Role.valueOf(requestDto.getRole().toUpperCase());
        String prefix = switch (role) {
            case ADMIN -> "ADM";
            case OFFICER -> "OFF";
            case CONSUMER -> "CON";
            default -> "USR";
        };

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
        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return toResponseDto(user);
    }

    @Cacheable(value = "users", key = "#publicId")
    public User getUserEntityByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + publicId));
    }

    @Override
    public UserResponseDto getUserByPublicId(String publicId) {
        return toResponseDto(getUserEntityByPublicId(publicId));
    }

    @Override
    @Transactional
    public UserResponseDto createConsumerActive(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + requestDto.getEmail());
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
        return toResponseDto(saved);
    }

    @Override
    public Page<UserResponseDto> getConsumers(Pageable pageable) {
        return userRepository.findByRole(Role.CONSUMER, pageable).map(this::toResponseDto);
    }

    @Override
    public Page<UserResponseDto> getConsumersByName(String name, Pageable pageable) {
        return userRepository.findByRoleAndUsernameContainingIgnoreCase(Role.CONSUMER, name, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#publicId")
    public UserResponseDto approveOfficer(String publicId) {
        User officer = getUserEntityByPublicId(publicId);
        officer.setActive(true);
        return toResponseDto(userRepository.save(officer));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#publicId")
    public void deleteUserByPublicId(String publicId) {
        User user = getUserEntityByPublicId(publicId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found");
        userRepository.deleteById(id);
    }

    private UserResponseDto toResponseDto(User user) {
        // Fetch existing meter from DB
        var meterData = meterRepository.findByUser(user).stream().findFirst()
                .map(m -> MeterResponseDto.builder()
                        .publicId(m.getPublicId())
                        .meterId(m.getMeterId())
                        .location(m.getLocation())
                        .status(m.getStatus())
                        .billing(m.getBilling())
                        .userPublicId(user.getPublicId())
                        .build())
                .orElse(null);

        return UserResponseDto.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .meter(meterData)
                .build();
    }
}