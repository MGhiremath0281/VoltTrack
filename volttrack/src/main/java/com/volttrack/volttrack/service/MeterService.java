package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;

import java.util.List;

public interface MeterService {
    MeterResponseDto createMeter(MeterRequestDto requestDto);
    List<MeterResponseDto> getAllMeters();
    MeterResponseDto getMeterById(Long id);
    void deleteMeter(Long id);
}
