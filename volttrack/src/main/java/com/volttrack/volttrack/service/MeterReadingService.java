package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {
    MeterReadingDTO saveReading(MeterReadingDTO dto);
    MeterReadingDTO getReadingById(Long id);
    Page<MeterReadingDTO> getAllReadings(Pageable pageable);
    void deleteReading(Long id);
}
