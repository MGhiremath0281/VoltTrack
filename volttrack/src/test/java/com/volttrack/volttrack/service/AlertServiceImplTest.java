package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.AlertRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.impl.AlertServiceImpl;
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
public class AlertServiceImplTest {

    @Mock private AlertRepository alertRepository;
    @Mock private MeterRepository meterRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AlertServiceImpl alertService;

    private User sampleUser;
    private Meter sampleMeter;
    private Alert sampleAlert;
    private AlertRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).publicId("CON-1").build();
        sampleMeter = Meter.builder().id(10L).user(sampleUser).build();

        sampleAlert = Alert.builder()
                .id(100L)
                .publicId("ALERT-100")
                .meter(sampleMeter)
                .alertType("OVER_VOLTAGE")
                .message("Voltage exceeded 240V")
                .createdAt(LocalDateTime.now())
                .build();

        sampleRequest = AlertRequestDto.builder()
                .meterId(10L)
                .alertType("OVER_VOLTAGE")
                .message("Voltage exceeded 240V")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createAlert: Should save alert and generate publicId based on ID")
    void testCreateAlert_Success() {
        when(meterRepository.findById(10L)).thenReturn(Optional.of(sampleMeter));
        // Mocking the first save to return an alert with an ID
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        AlertResponseDto response = alertService.createAlert(sampleRequest);

        assertNotNull(response);
        assertEquals("ALERT-100", response.getPublicId());
        verify(alertRepository, times(2)).save(any(Alert.class));
    }

    @Test
    @DisplayName("getAlertByPublicId: Should return alert when exists")
    void testGetAlertByPublicId_Success() {
        when(alertRepository.findByPublicId("ALERT-100")).thenReturn(Optional.of(sampleAlert));

        AlertResponseDto response = alertService.getAlertByPublicId("ALERT-100");

        assertEquals("OVER_VOLTAGE", response.getAlertType());
        assertEquals(100L, response.getId());
    }

    @Test
    @DisplayName("deleteAlertByPublicId: Should throw exception if not found")
    void testDeleteAlertByPublicId_NotFound() {
        when(alertRepository.findByPublicId("NOT-FOUND")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertService.deleteAlertByPublicId("NOT-FOUND"));
    }

    @Test
    @DisplayName("createAlertForConsumer: Should find meter via consumer publicId and save")
    void testCreateAlertForConsumer_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));
        when(meterRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleMeter));
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        AlertResponseDto response = alertService.createAlertForConsumer("CON-1", sampleRequest);

        assertNotNull(response);
        assertEquals("ALERT-100", response.getPublicId());
        verify(userRepository).findByPublicId("CON-1");
        verify(meterRepository).findByUser_Id(1L);
    }

    @Test
    @DisplayName("getAlertsByConsumer: Should return paginated alerts for specific consumer")
    void testGetAlertsByConsumer_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));
        when(alertRepository.findByMeter_User_Id(1L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(sampleAlert)));

        Page<AlertResponseDto> result = alertService.getAlertsByConsumer("CON-1", pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("ALERT-100", result.getContent().get(0).getPublicId());
    }

    @Test
    @DisplayName("deleteAlert: Should delete by Long ID if exists")
    void testDeleteAlert_Success() {
        when(alertRepository.existsById(100L)).thenReturn(true);

        alertService.deleteAlert(100L);

        verify(alertRepository).deleteById(100L);
    }

    @Test
    @DisplayName("getAllAlerts: Should return page of alerts")
    void testGetAllAlerts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Alert> page = new PageImpl<>(Collections.singletonList(sampleAlert));
        when(alertRepository.findAll(pageable)).thenReturn(page);

        Page<AlertResponseDto> result = alertService.getAllAlerts(pageable);

        assertEquals(1, result.getTotalElements());
        verify(alertRepository).findAll(pageable);
    }
}