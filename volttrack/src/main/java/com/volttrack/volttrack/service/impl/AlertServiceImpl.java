package com.volttrack.volttrack.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.AlertRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.AlertService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"allAlerts", "alertsByConsumer"}, allEntries = true)
    public AlertResponseDto createAlert(AlertRequestDto requestDto) {
        log.info("Creating alert for meterId={} with type={}", requestDto.getMeterId(), requestDto.getAlertType());

        Meter meter = meterRepository.findById(requestDto.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + requestDto.getMeterId()));

        Alert alert = Alert.builder()
                .meter(meter)
                .alertType(requestDto.getAlertType())
                .message(requestDto.getMessage())
                .createdAt(requestDto.getCreatedAt())
                .build();

        Alert saved = alertRepository.save(alert);
        saved.setPublicId("ALERT-" + saved.getId());
        alertRepository.save(saved);

        log.info("Alert created successfully with publicId={}", saved.getPublicId());
        return toResponseDto(saved);
    }

    @Override
    @Cacheable(value = "allAlerts", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<AlertResponseDto> getAllAlerts(Pageable pageable) {
        log.debug("Fetching all alerts with pagination");
        return alertRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Cacheable(value = "alertsById", key = "#id")
    public AlertResponseDto getAlertById(Long id) {
        log.info("Fetching alert with id={}", id);
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
        return toResponseDto(alert);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "alertsById", key = "#id"),
            @CacheEvict(value = "allAlerts", allEntries = true),
            @CacheEvict(value = "alertsByConsumer", allEntries = true)
    })
    public void deleteAlert(Long id) {
        log.info("Deleting alert with id={}", id);
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
        log.info("Alert deleted successfully with id={}", id);
    }

    @Override
    @Cacheable(value = "alertsByPublicId", key = "#publicId")
    public AlertResponseDto getAlertByPublicId(String publicId) {
        log.info("Fetching alert with publicId={}", publicId);
        Alert alert = alertRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with publicId: " + publicId));
        return toResponseDto(alert);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "alertsByPublicId", key = "#publicId"),
            @CacheEvict(value = "allAlerts", allEntries = true),
            @CacheEvict(value = "alertsByConsumer", allEntries = true)
    })
    public void deleteAlertByPublicId(String publicId) {
        log.info("Deleting alert with publicId={}", publicId);
        Alert alert = alertRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with publicId: " + publicId));
        alertRepository.delete(alert);
        log.info("Alert deleted successfully with publicId={}", publicId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allAlerts", "alertsByConsumer"}, allEntries = true)
    public AlertResponseDto createAlertForConsumer(String consumerPublicId, AlertRequestDto requestDto) {
        log.info("Creating alert for consumerPublicId={} with type={}", consumerPublicId, requestDto.getAlertType());

        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId));

        Meter meter = meterRepository.findByUser_Id(consumer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No meter found for consumer publicId: " + consumerPublicId));

        Alert alert = Alert.builder()
                .meter(meter)
                .alertType(requestDto.getAlertType())
                .message(requestDto.getMessage())
                .createdAt(requestDto.getCreatedAt())
                .build();

        Alert saved = alertRepository.save(alert);
        saved.setPublicId("ALERT-" + saved.getId());
        alertRepository.save(saved);

        return toResponseDto(saved);
    }

    @Override
    @Cacheable(value = "alertsByConsumer", key = "#consumerPublicId + '_' + #pageable.pageNumber")
    public Page<AlertResponseDto> getAlertsByConsumer(String consumerPublicId, Pageable pageable) {
        log.debug("Fetching alerts for consumerPublicId={}", consumerPublicId);

        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId));

        return alertRepository.findByMeter_User_Id(consumer.getId(), pageable).map(this::toResponseDto);
    }

    private AlertResponseDto toResponseDto(Alert alert) {
        return AlertResponseDto.builder()
                .id(alert.getId())
                .publicId(alert.getPublicId())
                .meterId(alert.getMeter().getId())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}