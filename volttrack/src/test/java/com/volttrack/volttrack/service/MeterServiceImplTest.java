package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.impl.MeterServiceImpl;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeterServiceImplTest {

    @Mock
    private MeterRepository meterRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeterServiceImpl meterService;

    private User sampleUser;
    private Meter sampleMeter;
    private MeterRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .publicId("CON-1")
                .username("testuser")
                .build();

        sampleMeter = Meter.builder()
                .id(100L)
                .publicId("MET-123")
                .meterId("ELE-456")
                .location("New York")
                .user(sampleUser)
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();

        sampleRequest = MeterRequestDto.builder()
                .userPublicId("CON-1")
                .location("New York")
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();
    }

    @Test
    @DisplayName("createMeter: Should save meter when user exists")
    void testCreateMeter_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));
        when(meterRepository.save(any(Meter.class))).thenReturn(sampleMeter);

        MeterResponseDto response = meterService.createMeter(sampleRequest);

        assertNotNull(response);
        assertEquals("MET-123", response.getPublicId());
        assertEquals("CON-1", response.getUserPublicId());
        verify(userRepository).findByPublicId("CON-1");
        verify(meterRepository).save(any(Meter.class));
    }

    @Test
    @DisplayName("createMeter: Should throw exception when user is not found")
    void testCreateMeter_UserNotFound() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> meterService.createMeter(sampleRequest));
        verify(meterRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllMeters: Should return paginated meters")
    void testGetAllMeters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Meter> meterPage = new PageImpl<>(Collections.singletonList(sampleMeter));
        when(meterRepository.findAll(pageable)).thenReturn(meterPage);

        Page<MeterResponseDto> result = meterService.getAllMeters(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("ELE-456", result.getContent().get(0).getMeterId());
    }

    @Test
    @DisplayName("getMeterById: Should return meter DTO")
    void testGetMeterById_Success() {
        when(meterRepository.findById(100L)).thenReturn(Optional.of(sampleMeter));

        MeterResponseDto response = meterService.getMeterById(100L);

        assertEquals(100L, response.getId());
        assertEquals("New York", response.getLocation());
    }

    @Test
    @DisplayName("getMeterByPublicId: Should return meter DTO")
    void testGetMeterByPublicId_Success() {
        when(meterRepository.findByPublicId("MET-123")).thenReturn(Optional.of(sampleMeter));

        MeterResponseDto response = meterService.getMeterByPublicId("MET-123");

        assertEquals("MET-123", response.getPublicId());
    }

    @Test
    @DisplayName("deleteMeterByPublicId: Should delete when exists")
    void testDeleteMeterByPublicId_Success() {
        when(meterRepository.findByPublicId("MET-123")).thenReturn(Optional.of(sampleMeter));

        meterService.deleteMeterByPublicId("MET-123");

        verify(meterRepository).delete(sampleMeter);
    }

    @Test
    @DisplayName("deleteMeter: Should delete by Long ID")
    void testDeleteMeter_Success() {
        when(meterRepository.existsById(100L)).thenReturn(true);

        meterService.deleteMeter(100L);

        verify(meterRepository).deleteById(100L);
    }

    @Test
    @DisplayName("deleteMeter: Should throw exception if ID not found")
    void testDeleteMeter_NotFound() {
        when(meterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> meterService.deleteMeter(999L));
    }

    @Test
    @DisplayName("assignMeterToConsumer: Should force ONLINE status and save")
    void testAssignMeterToConsumer_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));

        // We use thenAnswer to verify that the logic forces ONLINE status before saving
        when(meterRepository.save(any(Meter.class))).thenAnswer(invocation -> {
            Meter m = invocation.getArgument(0);
            m.setId(200L); // simulate DB saving
            return m;
        });

        MeterResponseDto response = meterService.assignMeterToConsumer("CON-1", sampleRequest);

        assertNotNull(response);
        assertEquals(Status.ONLINE, response.getStatus());
        verify(userRepository).findByPublicId("CON-1");
        verify(meterRepository).save(any(Meter.class));
    }
}