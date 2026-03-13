package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.repository.AlertRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.AlertService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final MeterRepository meterRepository;

    public AlertServiceImpl(AlertRepository alertRepository, MeterRepository meterRepository) {
        this.alertRepository = alertRepository;
        this.meterRepository = meterRepository;
    }

    @Override
    public AlertResponseDto createAlert(AlertRequestDto requestDto) {
        Meter meter = meterRepository.findById(requestDto.getMeterId())
                .orElseThrow(() -> new RuntimeException("Meter not found"));

        Alert alert = Alert.builder()
                .meter(meter)
                .alertType(requestDto.getAlertType())
                .message(requestDto.getMessage())
                .createdAt(requestDto.getCreatedAt())
                .build();

        Alert saved = alertRepository.save(alert);
        return toResponseDto(saved);
    }

    @Override
    public List<AlertResponseDto> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlertResponseDto getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        return toResponseDto(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }

    private AlertResponseDto toResponseDto(Alert alert) {
        return AlertResponseDto.builder()
                .id(alert.getId())
                .meterId(alert.getMeter().getId())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
