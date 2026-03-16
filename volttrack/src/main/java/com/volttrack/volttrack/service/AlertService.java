package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {
    AlertResponseDto createAlert(AlertRequestDto requestDto);
    Page<AlertResponseDto> getAllAlerts(Pageable pageable);

    // Legacy numeric ID methods (still useful internally)
    AlertResponseDto getAlertById(Long id);
    void deleteAlert(Long id);

    AlertResponseDto getAlertByPublicId(String publicId);
    void deleteAlertByPublicId(String publicId);

    // Consumer‑specific methods using publicId
    AlertResponseDto createAlertForConsumer(String consumerPublicId, AlertRequestDto requestDto);
    Page<AlertResponseDto> getAlertsByConsumer(String consumerPublicId, Pageable pageable);
}
