package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Meter management.
 * Optimized for VoltTrack Field Operations.
 */
public interface MeterService {

    MeterResponseDto createMeter(MeterRequestDto requestDto);

    Page<MeterResponseDto> getAllMeters(Pageable pageable);

    List<MeterResponseDto> getMetersByUserPublicId(String publicId);

    MeterResponseDto getMeterById(Long id);

    MeterResponseDto getMeterByPublicId(String publicId);

    void deleteMeter(Long id);

    void deleteMeterByPublicId(String publicId);

    MeterResponseDto assignMeterToConsumer(String consumerPublicId, MeterRequestDto requestDto);
}