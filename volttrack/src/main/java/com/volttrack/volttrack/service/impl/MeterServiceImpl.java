package com.volttrack.volttrack.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.MeterService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    public MeterServiceImpl(MeterRepository meterRepository, UserRepository userRepository) {
        this.meterRepository = meterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MeterResponseDto createMeter(MeterRequestDto requestDto) {
        log.info("Creating new meter with meterId={} for userId={}", requestDto.getMeterId(), requestDto.getUserId());

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id={}", requestDto.getUserId());
                    return new ResourceNotFoundException("User not found with id: " + requestDto.getUserId());
                });

        Meter meter = Meter.builder()
                .meterId(requestDto.getMeterId())
                .location(requestDto.getLocation())
                .user(user)
                .status(requestDto.getStatus())
                .billing(requestDto.getBilling())
                .build();

        Meter saved = meterRepository.save(meter);
        log.info("Meter created successfully with id={}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    public Page<MeterResponseDto> getAllMeters(Pageable pageable) {
        log.debug("Fetching all meters with pagination");
        return meterRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    public MeterResponseDto getMeterById(Long id) {
        log.info("Fetching meter with id={}", id);
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Meter not found with id={}", id);
                    return new ResourceNotFoundException("Meter not found with id: " + id);
                });
        log.info("Meter found with id={}", id);
        return toResponseDto(meter);
    }

    @Override
    public void deleteMeter(Long id) {
        log.info("Deleting meter with id={}", id);
        if (!meterRepository.existsById(id)) {
            log.error("Cannot delete. Meter not found with id={}", id);
            throw new ResourceNotFoundException("Cannot delete. Meter not found with id: " + id);
        }
        meterRepository.deleteById(id);
        log.info("Meter deleted successfully with id={}", id);
    }

    private MeterResponseDto toResponseDto(Meter meter) {
        return MeterResponseDto.builder()
                .id(meter.getId())
                .meterId(meter.getMeterId())
                .location(meter.getLocation())
                .userId(meter.getUser().getId())
                .status(meter.getStatus())
                .billing(meter.getBilling())
                .build();
    }
}
