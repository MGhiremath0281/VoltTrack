package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.impl.MeterReadingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeterReadingServiceImplTest {

    @Mock
    private MeterReadingRepository meterReadingRepository;

    @Mock
    private MeterRepository meterRepository;

    @InjectMocks
    private MeterReadingServiceImpl meterReadingService;

    private Meter sampleMeter;
    private MeterReading sampleReading;
    private MeterReadingDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleMeter = Meter.builder()
                .id(1L)
                .publicId("MET-123")
                .build();

        sampleReading = MeterReading.builder()
                .id(10L)
                .publicId("READ-abc12345")
                .meter(sampleMeter)
                .pulseCount(150)
                .voltage(230.5)
                .current(5.2)
                .unitsConsumed(1.5)
                .timestamp(LocalDateTime.now())
                .build();

        sampleDto = MeterReadingDTO.builder()
                .meterPublicId("MET-123")
                .pulseCount(150)
                .voltage(230.5)
                .current(5.2)
                .unitsConsumed(1.5)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("saveReading: Should successfully map DTO and save entity")
    void testSaveReading_Success() {
        when(meterRepository.findByPublicId("MET-123")).thenReturn(Optional.of(sampleMeter));

        when(meterReadingRepository.save(any(MeterReading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeterReadingDTO result = meterReadingService.saveReading(sampleDto);

        assertNotNull(result);
        assertTrue(result.getPublicId().startsWith("READ-"), "PublicId should have READ- prefix");
        assertEquals(150, result.getPulseCount());
        assertEquals("MET-123", result.getMeterPublicId());

        verify(meterRepository).findByPublicId("MET-123");
        verify(meterReadingRepository).save(any(MeterReading.class));
    }

    @Test
    @DisplayName("getReadingByPublicId: Should return mapped DTO")
    void testGetReadingByPublicId_Success() {
        String publicId = "READ-abc12345";
        when(meterReadingRepository.findByPublicId(publicId)).thenReturn(Optional.of(sampleReading));

        MeterReadingDTO result = meterReadingService.getReadingByPublicId(publicId);

        assertNotNull(result);
        assertEquals(publicId, result.getPublicId());
        assertEquals(230.5, result.getVoltage());
    }

    @Test
    @DisplayName("getReadingByPublicId: Should throw exception for invalid ID")
    void testGetReadingByPublicId_NotFound() {
        when(meterReadingRepository.findByPublicId("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> meterReadingService.getReadingByPublicId("UNKNOWN"));
    }

    @Test
    @DisplayName("getAllReadings: Should handle pagination correctly")
    void testGetAllReadings() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeterReading> page = new PageImpl<>(Collections.singletonList(sampleReading));
        when(meterReadingRepository.findAll(pageable)).thenReturn(page);

        Page<MeterReadingDTO> result = meterReadingService.getAllReadings(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("MET-123", result.getContent().get(0).getMeterPublicId());
    }

    @Test
    @DisplayName("deleteReadingByPublicId: Should call repository delete")
    void testDeleteReadingByPublicId_Success() {
        String publicId = "READ-abc12345";
        when(meterReadingRepository.findByPublicId(publicId)).thenReturn(Optional.of(sampleReading));

        meterReadingService.deleteReadingByPublicId(publicId);

        verify(meterReadingRepository, times(1)).delete(sampleReading);
    }
}