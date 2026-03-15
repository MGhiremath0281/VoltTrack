package com.volttrack.volttrack.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.MeterReadingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;

    @Override
    public MeterReadingDTO saveReading(MeterReadingDTO dto) {
        log.info("Saving new meter reading for meterId={}", dto.getMeterId());

        Meter meter = meterRepository.findById(dto.getMeterId())
                .orElseThrow(() -> {
                    log.error("Meter not found with id={}", dto.getMeterId());
                    return new ResourceNotFoundException("Meter not found with id: " + dto.getMeterId());
                });

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
        log.info("Meter reading saved successfully with id={}", saved.getId());

        return toDTO(saved);
    }

    @Override
    public MeterReadingDTO getReadingById(Long id) {
        log.info("Fetching meter reading with id={}", id);
        MeterReading reading = meterReadingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Meter reading not found with id={}", id);
                    return new ResourceNotFoundException("Meter reading not found with id: " + id);
                });
        log.info("Meter reading found with id={}", id);
        return toDTO(reading);
    }

    @Override
    public Page<MeterReadingDTO> getAllReadings(Pageable pageable) {
        log.debug("Fetching all meter readings with pagination");
        return meterReadingRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public void deleteReading(Long id) {
        log.info("Deleting meter reading with id={}", id);
        if (!meterReadingRepository.existsById(id)) {
            log.error("Cannot delete. Meter reading not found with id={}", id);
            throw new ResourceNotFoundException("Cannot delete. Meter reading not found with id: " + id);
        }
        meterReadingRepository.deleteById(id);
        log.info("Meter reading deleted successfully with id={}", id);
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
