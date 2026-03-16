package com.volttrack.volttrack.service.impl;

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
        Meter meter = meterRepository.findById(dto.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + dto.getMeterId()));

        MeterReading entity = MeterReading.builder()
                .meter(meter)
                .pulseCount(dto.getPulseCount())
                .voltage(dto.getVoltage())
                .current(dto.getCurrent())
                .unitsConsumed(dto.getUnitsConsumed())
                .timestamp(dto.getTimestamp())
                .build();

        MeterReading saved = meterReadingRepository.save(entity);

        // Generate prefixed publicId (READ-<id>)
        saved.setPublicId("READ-" + saved.getId());
        meterReadingRepository.save(saved);

        log.info("Meter reading saved successfully with publicId={}", saved.getPublicId());
        return toDTO(saved);
    }


    @Override
    public MeterReadingDTO getReadingByPublicId(String readingPublicId) {
        log.info("Fetching meter reading with publicId={}", readingPublicId);
        MeterReading reading = meterReadingRepository.findByPublicId(readingPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId));
        return toDTO(reading);
    }

    @Override
    public Page<MeterReadingDTO> getAllReadings(Pageable pageable) {
        log.debug("Fetching all meter readings with pagination");
        return meterReadingRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public void deleteReadingByPublicId(String readingPublicId) {
        log.info("Deleting meter reading with publicId={}", readingPublicId);
        MeterReading reading = meterReadingRepository.findByPublicId(readingPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId));
        meterReadingRepository.delete(reading);
        log.info("Meter reading deleted successfully with publicId={}", readingPublicId);
    }

    private MeterReadingDTO toDTO(MeterReading entity) {
        return MeterReadingDTO.builder()
                .id(entity.getId())
                .publicId(entity.getPublicId())   // ✅ include publicId
                .meterId(entity.getMeter().getId())
                .pulseCount(entity.getPulseCount())
                .voltage(entity.getVoltage())
                .current(entity.getCurrent())
                .unitsConsumed(entity.getUnitsConsumed())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
