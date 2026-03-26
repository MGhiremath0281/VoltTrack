package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeterService {
    MeterResponseDto createMeter(MeterRequestDto requestDto);
    Page<MeterResponseDto> getAllMeters(Pageable pageable);
    List<MeterResponseDto> getMetersByUserPublicId(String publicId);

    // Legacy numeric ID methods
    MeterResponseDto getMeterById(Long id);
    void deleteMeter(Long id);

    MeterResponseDto getMeterByPublicId(String publicId);
    void deleteMeterByPublicId(String publicId);

    // Consumer‑specific methods using publicId
    MeterResponseDto assignMeterToConsumer(String consumerPublicId, MeterRequestDto requestDto);
}
