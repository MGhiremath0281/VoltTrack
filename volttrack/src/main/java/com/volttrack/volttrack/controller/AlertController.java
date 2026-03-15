package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<AlertResponseDto> createAlert(@Valid @RequestBody AlertRequestDto requestDto) {
        AlertResponseDto saved = alertService.createAlert(requestDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<AlertResponseDto>> getAllAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @GetMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #alertId == authentication.principal.id")
    public ResponseEntity<AlertResponseDto> getAlertById(@PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.getAlertById(alertId));
    }

    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}
