package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterService {
    MeterResponseDto createMeter(MeterRequestDto requestDto);
    Page<MeterResponseDto> getAllMeters(Pageable pageable);
    MeterResponseDto getMeterById(Long id);
    void deleteMeter(Long id);

    MeterResponseDto assignMeterToConsumer(String consumerPublicId, MeterRequestDto requestDto);
}
