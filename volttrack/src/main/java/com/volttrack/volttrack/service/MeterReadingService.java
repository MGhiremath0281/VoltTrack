package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeterReadingService {
    MeterReadingDTO saveReading(MeterReadingDTO dto);
    MeterReadingDTO getReadingByPublicId(String readingPublicId);
    Page<MeterReadingDTO> getAllReadings(Pageable pageable);
    void deleteReadingByPublicId(String readingPublicId);
    List<MeterReadingDTO> getReadingsByUserPublicId(String publicId);

    MeterReadingDTO saveReadingForUser(MeterReadingDTO dto, String publicId);
}
