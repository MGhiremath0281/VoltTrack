package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {
    AlertResponseDto createAlert(AlertRequestDto requestDto);
    Page<AlertResponseDto> getAllAlerts(Pageable pageable);
    AlertResponseDto getAlertById(Long id);
    void deleteAlert(Long id);
}
