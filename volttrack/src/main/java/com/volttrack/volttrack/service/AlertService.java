package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;

import java.util.List;

public interface AlertService {
    AlertResponseDto createAlert(AlertRequestDto requestDto);
    List<AlertResponseDto> getAllAlerts();
    AlertResponseDto getAlertById(Long id);
    void deleteAlert(Long id);
}
