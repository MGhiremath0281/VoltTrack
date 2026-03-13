package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository repository;

    @Override
    public MeterReadingDTO saveReading(MeterReadingDTO dto) {
        MeterReading entity = MeterReading.builder()
                .id(dto.getId())
                .meterId(dto.getMeterId())
                .pulseCount(dto.getPulseCount())
                .voltage(dto.getVoltage())
                .current(dto.getCurrent())
                .unitsConsumed(dto.getUnitsConsumed())
                .timestamp(dto.getTimestamp())
                .build();

        MeterReading saved = repository.save(entity);
        return toDTO(saved);
    }

    @Override
    public MeterReadingDTO getReadingById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public List<MeterReadingDTO> getAllReadings() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReading(Long id) {
        repository.deleteById(id);
    }

    private MeterReadingDTO toDTO(MeterReading entity) {
        return MeterReadingDTO.builder()
                .id(entity.getId())
                .meterId(entity.getMeterId())
                .pulseCount(entity.getPulseCount())
                .voltage(entity.getVoltage())
                .current(entity.getCurrent())
                .unitsConsumed(entity.getUnitsConsumed())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
