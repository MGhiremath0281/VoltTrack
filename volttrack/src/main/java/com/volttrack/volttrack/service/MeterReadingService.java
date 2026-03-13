package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import java.util.List;

public interface MeterReadingService {
    MeterReadingDTO saveReading(MeterReadingDTO meterReadingDTO);
    MeterReadingDTO getReadingById(Long id);
    List<MeterReadingDTO> getAllReadings();
    void deleteReading(Long id);
}
