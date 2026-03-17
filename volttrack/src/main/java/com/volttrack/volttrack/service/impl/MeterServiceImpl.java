package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.MeterService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        log.info("Creating meter for userPublicId={}", requestDto.getUserPublicId());

        User user = userRepository.findByPublicId(requestDto.getUserPublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with publicId: " + requestDto.getUserPublicId()));

        Meter meter = Meter.builder()
                .location(requestDto.getLocation())
                .user(user)
                .status(requestDto.getStatus())
                .billing(requestDto.getBilling())
                .build();

        Meter saved = meterRepository.save(meter);

        log.info("Meter created successfully with publicId={} and meterId={}",
                saved.getPublicId(), saved.getMeterId());

        return toResponseDto(saved);
    }

    @Override
    public Page<MeterResponseDto> getAllMeters(Pageable pageable) {
        return meterRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    public MeterResponseDto getMeterById(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with id: " + id));
        return toResponseDto(meter);
    }

    @Override
    public MeterResponseDto getMeterByPublicId(String publicId) {
        Meter meter = meterRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with publicId: " + publicId));
        return toResponseDto(meter);
    }

    @Override
    public void deleteMeterByPublicId(String publicId) {
        Meter meter = meterRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with publicId: " + publicId));
        meterRepository.delete(meter);
    }

    @Override
    public void deleteMeter(Long id) {
        if (!meterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Meter not found with id: " + id);
        }
        meterRepository.deleteById(id);
    }

    @Override
    public MeterResponseDto assignMeterToConsumer(String consumerPublicId, MeterRequestDto requestDto) {

        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId));

        Meter meter = Meter.builder()
                .location(requestDto.getLocation())
                .user(consumer)
                .status(Status.ONLINE)
                .billing(requestDto.getBilling())
                .build();

        Meter saved = meterRepository.save(meter);

        return toResponseDto(saved);
    }

    private MeterResponseDto toResponseDto(Meter meter) {
        return MeterResponseDto.builder()
                .id(meter.getId())
                .publicId(meter.getPublicId())
                .meterId(meter.getMeterId())
                .location(meter.getLocation())
                .userPublicId(meter.getUser().getPublicId())
                .status(meter.getStatus())
                .billing(meter.getBilling())
                .build();
    }
}