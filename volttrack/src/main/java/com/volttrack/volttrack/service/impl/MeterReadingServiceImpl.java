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

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;

    @Override
    public MeterReadingDTO saveReading(MeterReadingDTO dto) {

        // ✅ Fetch meter using publicId
        Meter meter = meterRepository.findByPublicId(dto.getMeterPublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with publicId: " + dto.getMeterPublicId())
                );

        // ✅ Generate publicId BEFORE saving (FIX 🔥)
        String publicId = "READ-" + UUID.randomUUID().toString().substring(0, 8);

        MeterReading entity = MeterReading.builder()
                .publicId(publicId)
                .meter(meter)
                .pulseCount(dto.getPulseCount())
                .voltage(dto.getVoltage())
                .current(dto.getCurrent())
                .unitsConsumed(dto.getUnitsConsumed())
                .timestamp(dto.getTimestamp())
                .build();

        // ✅ Single DB call
        MeterReading saved = meterReadingRepository.save(entity);

        log.info("Meter reading saved successfully with publicId={}", saved.getPublicId());
        return toDTO(saved);
    }

    @Override
    public MeterReadingDTO getReadingByPublicId(String readingPublicId) {
        MeterReading reading = meterReadingRepository.findByPublicId(readingPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId)
                );
        return toDTO(reading);
    }

    @Override
    public Page<MeterReadingDTO> getAllReadings(Pageable pageable) {
        return meterReadingRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public void deleteReadingByPublicId(String readingPublicId) {
        MeterReading reading = meterReadingRepository.findByPublicId(readingPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId)
                );
        meterReadingRepository.delete(reading);
    }

    // ✅ DTO Mapping
    private MeterReadingDTO toDTO(MeterReading entity) {
        return MeterReadingDTO.builder()
                .publicId(entity.getPublicId())
                .meterPublicId(entity.getMeter().getPublicId())
                .pulseCount(entity.getPulseCount())
                .voltage(entity.getVoltage())
                .current(entity.getCurrent())
                .unitsConsumed(entity.getUnitsConsumed())
                .timestamp(entity.getTimestamp())
                .build();
    }
}