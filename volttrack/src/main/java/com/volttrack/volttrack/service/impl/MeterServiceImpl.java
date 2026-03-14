package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.ResourceNotFoundException; 
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.MeterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    public MeterServiceImpl(MeterRepository meterRepository, UserRepository userRepository) {
        this.meterRepository = meterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MeterResponseDto createMeter(MeterRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        Meter meter = Meter.builder()
                .meterId(requestDto.getMeterId())
                .location(requestDto.getLocation())
                .user(user)
                .status(requestDto.getStatus())
                .billing(requestDto.getBilling())
                .build();

        Meter saved = meterRepository.save(meter);
        return toResponseDto(saved);
    }

    @Override
    public List<MeterResponseDto> getAllMeters() {
        return meterRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MeterResponseDto getMeterById(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + id));
        return toResponseDto(meter);
    }

    @Override
    public void deleteMeter(Long id) {
        if (!meterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Meter not found with id: " + id);
        }
        meterRepository.deleteById(id);
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
