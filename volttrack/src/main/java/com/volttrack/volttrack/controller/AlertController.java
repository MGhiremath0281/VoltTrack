package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;


    @Operation(summary = "Create alert", description = "Create a new alert. Only ADMIN and OFFICER roles are allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN or OFFICER role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<AlertResponseDto> createAlert(@Valid @RequestBody AlertRequestDto requestDto) {
        AlertResponseDto saved = alertService.createAlert(requestDto);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Get all alerts", description = "Retrieve a paginated list of all alerts. Accessible by ADMIN and OFFICER.")
    @ApiResponse(responseCode = "200", description = "List of alerts retrieved successfully")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<AlertResponseDto>> getAllAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @Operation(summary = "Get alert by ID", description = "Retrieve a specific alert by ID. Accessible by ADMIN, OFFICER, or the alert owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert found"),
            @ApiResponse(responseCode = "404", description = "Alert not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #alertId == authentication.principal.id")
    public ResponseEntity<AlertResponseDto> getAlertById(@PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.getAlertById(alertId));
    }

    @Operation(summary = "Delete alert", description = "Delete an alert by ID. Only ADMIN can delete alerts.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Alert not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN role")
    })
    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}
