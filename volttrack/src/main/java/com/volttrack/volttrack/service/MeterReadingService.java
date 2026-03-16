package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {
    MeterReadingDTO saveReading(MeterReadingDTO dto);
    MeterReadingDTO getReadingByPublicId(String readingPublicId);
    Page<MeterReadingDTO> getAllReadings(Pageable pageable);
    void deleteReadingByPublicId(String readingPublicId);
}
