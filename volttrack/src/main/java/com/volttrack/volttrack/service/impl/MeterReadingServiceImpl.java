package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.exception.ResourceNotFoundException; // <-- custom exception
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;

    @Override
    public MeterReadingDTO saveReading(MeterReadingDTO dto) {
        Meter meter = meterRepository.findById(dto.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + dto.getMeterId()));

        MeterReading entity = MeterReading.builder()
                .id(dto.getId())
                .meter(meter)
                .pulseCount(dto.getPulseCount())
                .voltage(dto.getVoltage())
                .current(dto.getCurrent())
                .unitsConsumed(dto.getUnitsConsumed())
                .timestamp(dto.getTimestamp())
                .build();

        MeterReading saved = meterReadingRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public MeterReadingDTO getReadingById(Long id) {
        MeterReading reading = meterReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with id: " + id));
        return toDTO(reading);
    }

    @Override
    public List<MeterReadingDTO> getAllReadings() {
        return meterReadingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReading(Long id) {
        if (!meterReadingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Meter reading not found with id: " + id);
        }
        meterReadingRepository.deleteById(id);
    }

    // --- Helper method to convert entity -> DTO ---
    private MeterReadingDTO toDTO(MeterReading entity) {
        return MeterReadingDTO.builder()
                .id(entity.getId())
                .meterId(entity.getMeter().getId())
                .pulseCount(entity.getPulseCount())
                .voltage(entity.getVoltage())
                .current(entity.getCurrent())
                .unitsConsumed(entity.getUnitsConsumed())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
