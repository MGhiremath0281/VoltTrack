package com.volttrack.volttrack.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.AlertRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.AlertService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final MeterRepository meterRepository;

    public AlertServiceImpl(AlertRepository alertRepository, MeterRepository meterRepository) {
        this.alertRepository = alertRepository;
        this.meterRepository = meterRepository;
    }

    @Override
    public AlertResponseDto createAlert(AlertRequestDto requestDto) {
        log.info("Creating alert for meterId={} with type={}", requestDto.getMeterId(), requestDto.getAlertType());

        Meter meter = meterRepository.findById(requestDto.getMeterId())
                .orElseThrow(() -> {
                    log.error("Meter not found with id={}", requestDto.getMeterId());
                    return new ResourceNotFoundException("Meter not found with id: " + requestDto.getMeterId());
                });

        Alert alert = Alert.builder()
                .meter(meter)
                .alertType(requestDto.getAlertType())
                .message(requestDto.getMessage())
                .createdAt(requestDto.getCreatedAt())
                .build();

        Alert saved = alertRepository.save(alert);
        log.info("Alert created successfully with id={}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    public Page<AlertResponseDto> getAllAlerts(Pageable pageable) {
        log.debug("Fetching all alerts with pagination");
        return alertRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    public AlertResponseDto getAlertById(Long id) {
        log.info("Fetching alert with id={}", id);
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Alert not found with id={}", id);
                    return new ResourceNotFoundException("Alert not found with id: " + id);
                });
        log.info("Alert found with id={}", id);
        return toResponseDto(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        log.info("Deleting alert with id={}", id);
        if (!alertRepository.existsById(id)) {
            log.error("Cannot delete. Alert not found with id={}", id);
            throw new ResourceNotFoundException("Cannot delete. Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
        log.info("Alert deleted successfully with id={}", id);
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
