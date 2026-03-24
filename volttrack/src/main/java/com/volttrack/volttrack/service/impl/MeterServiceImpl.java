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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @CacheEvict(value = "metersList", allEntries = true)
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
        return toResponseDto(saved);
    }

    @Override
    @Cacheable(value = "metersList", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MeterResponseDto> getAllMeters(Pageable pageable) {
        return meterRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Cacheable(value = "metersById", key = "#id")
    public MeterResponseDto getMeterById(Long id) {
        return meterRepository.findById(id)
                .map(this::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + id));
    }

    @Override
    @Cacheable(value = "metersByPublicId", key = "#publicId")
    public MeterResponseDto getMeterByPublicId(String publicId) {
        return meterRepository.findByPublicId(publicId)
                .map(this::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with publicId: " + publicId));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "metersByPublicId", key = "#publicId"),
            @CacheEvict(value = "metersList", allEntries = true)
    })
    public void deleteMeterByPublicId(String publicId) {
        Meter meter = meterRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));
        meterRepository.delete(meter);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "metersById", key = "#id"),
            @CacheEvict(value = "metersList", allEntries = true)
    })
    public void deleteMeter(Long id) {
        if (!meterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Meter not found");
        }
        meterRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "metersList", allEntries = true)
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