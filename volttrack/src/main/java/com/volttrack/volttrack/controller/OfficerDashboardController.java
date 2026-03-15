package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.service.AlertService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officer/dashboard")
@PreAuthorize("hasRole('OFFICER')")
public class OfficerDashboardController {

    private final UserService userService;
    private final MeterService meterService;
    private final BillService billService;
    private final AlertService alertService;

    public OfficerDashboardController(UserService userService,
                                      MeterService meterService,
                                      BillService billService,
                                      AlertService alertService) {
        this.userService = userService;
        this.meterService = meterService;
        this.billService = billService;
        this.alertService = alertService;
    }

    // Consumer Management
    @PostMapping("/consumers")
    public ResponseEntity<UserResponseDto> createConsumer(@RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.createConsumerActive(dto));
    }

    @GetMapping("/consumers")
    public ResponseEntity<Page<UserResponseDto>> getConsumers(Pageable pageable) {
        return ResponseEntity.ok(userService.getConsumers(pageable));
    }

    // Meter Management
    @PostMapping("/meters/assign/{consumerId}")
    public ResponseEntity<MeterResponseDto> assignMeter(@PathVariable Long consumerId,
                                                        @RequestBody MeterRequestDto dto) {
        return ResponseEntity.ok(meterService.assignMeterToConsumer(consumerId, dto));
    }

    // Billing
    @PostMapping("/bills/{consumerId}")
    public ResponseEntity<BillResponseDto> generateBill(@PathVariable Long consumerId) {
        return ResponseEntity.ok(billService.generateBillForConsumer(consumerId));
    }

    // Alerts
    @PostMapping("/alerts/{consumerId}")
    public ResponseEntity<AlertResponseDto> createAlert(@PathVariable Long consumerId,
                                                        @RequestBody AlertRequestDto dto) {
        return ResponseEntity.ok(alertService.createAlertForConsumer(consumerId, dto));
    }
}
