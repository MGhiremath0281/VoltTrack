package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.MeterReadingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "allReadings", allEntries = true)
    public MeterReadingDTO saveReading(MeterReadingDTO dto) {
        log.info("Saving new reading for meterPublicId={}", dto.getMeterPublicId());

        Meter meter = meterRepository.findByPublicId(dto.getMeterPublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with publicId: " + dto.getMeterPublicId())
                );

        String publicId = "READ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        MeterReading entity = MeterReading.builder()
                .publicId(publicId)
                .meter(meter)
                .pulseCount(dto.getPulseCount())
                .voltage(dto.getVoltage())
                .current(dto.getCurrent())
                .unitsConsumed(dto.getUnitsConsumed())
                .timestamp(dto.getTimestamp())
                .build();

        MeterReading saved = meterReadingRepository.save(entity);

        log.info("Meter reading saved successfully with publicId={}", saved.getPublicId());
        return toDTO(saved);
    }

    @Override
    @Cacheable(value = "readingsByPublicId", key = "#readingPublicId")
    public MeterReadingDTO getReadingByPublicId(String readingPublicId) {
        log.debug("Fetching reading from DB/Cache for publicId={}", readingPublicId);
        return meterReadingRepository.findByPublicId(readingPublicId)
                .map(this::toDTO)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId)
                );
    }

    @Override
    @Cacheable(value = "allReadings", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MeterReadingDTO> getAllReadings(Pageable pageable) {
        log.debug("Fetching paginated readings");
        return meterReadingRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "readingsByPublicId", key = "#readingPublicId"),
            @CacheEvict(value = "allReadings", allEntries = true)
    })
    public void deleteReadingByPublicId(String readingPublicId) {
        log.info("Deleting reading with publicId={}", readingPublicId);
        MeterReading reading = meterReadingRepository.findByPublicId(readingPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter reading not found with publicId: " + readingPublicId)
                );
        meterReadingRepository.delete(reading);
    }

    @Override
    public List<MeterReadingDTO> getReadingsByUserPublicId(String publicId) {

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Meter> meters = meterRepository.findByUser(user);

        List<MeterReadingDTO> result = new ArrayList<>();

        for (Meter meter : meters) {

            meterReadingRepository
                    .findTopByMeter_IdOrderByTimestampDesc(meter.getId())
                    .ifPresent(reading -> result.add(toDTO(reading)));
        }

        return result;
    }

    @Override
    public MeterReadingDTO saveReadingForUser(MeterReadingDTO dto, String publicId) {

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Meter meter = meterRepository.findByPublicId(dto.getMeterPublicId())
                .orElseThrow(() -> new RuntimeException("Meter not found"));

        // 🔐 Ownership check
        if (!meter.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        MeterReading reading = new MeterReading();
        reading.setMeter(meter);
        reading.setUnitsConsumed(dto.getUnitsConsumed());
        reading.setTimestamp(dto.getTimestamp());

        MeterReading saved = meterReadingRepository.save(reading);

        return toDTO(saved);
    }

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