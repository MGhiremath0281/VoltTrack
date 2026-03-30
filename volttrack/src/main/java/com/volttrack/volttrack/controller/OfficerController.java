package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.alert.AlertRequestDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.security.CustomUserDetails;
import com.volttrack.volttrack.security.CustomUserPrincipal;
import com.volttrack.volttrack.service.AlertService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officer")
@PreAuthorize("hasRole('OFFICER')")
public class OfficerController {

    private final UserService userService;
    private final MeterService meterService;
    private final BillService billService;
    private final AlertService alertService;

    public OfficerController(UserService userService,
                             MeterService meterService,
                             BillService billService,
                             AlertService alertService) {
        this.userService = userService;
        this.meterService = meterService;
        this.billService = billService;
        this.alertService = alertService;
    }

    @Operation(summary = "Get logged-in officer details")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getLoggedInOfficer(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                userService.getUserByPublicId(userDetails.getPublicId())
        );
    }

    @Operation(summary = "Create a new consumer")
    @PostMapping("/consumers")
    public ResponseEntity<UserResponseDto> createConsumer(@RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.createConsumerActive(dto));
    }

    @Operation(summary = "Get all consumers")
    @GetMapping("/consumers")
    public ResponseEntity<Page<UserResponseDto>> getConsumers(Pageable pageable) {
        return ResponseEntity.ok(userService.getConsumers(pageable));
    }

    @Operation(summary = "Search consumers by name")
    @GetMapping("/consumers/search")
    public ResponseEntity<Page<UserResponseDto>> getConsumersByName(@RequestParam String name,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(userService.getConsumersByName(name, pageable));
    }

    @Operation(summary = "Get user by public ID")
    @GetMapping("/users/{publicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #publicId == authentication.principal.publicId")
    public ResponseEntity<UserResponseDto> getUserByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(userService.getUserByPublicId(publicId));
    }

    @Operation(summary = "Assign meter to a consumer")
    @PostMapping("/meters/{consumerPublicId}")
    public ResponseEntity<MeterResponseDto> assignMeter(@PathVariable String consumerPublicId,
                                                        @RequestBody MeterRequestDto dto) {
        return ResponseEntity.ok(meterService.assignMeterToConsumer(consumerPublicId, dto));
    }

    @Operation(summary = "Generate bill for a consumer")
    @PostMapping("/bills/{consumerPublicId}")
    public ResponseEntity<BillResponseDto> generateBill(@PathVariable String consumerPublicId) {
        return ResponseEntity.ok(billService.generateBillForConsumer(consumerPublicId));
    }

    @Operation(summary = "Create alert for a consumer")
    @PostMapping("/alerts/{consumerPublicId}")
    public ResponseEntity<AlertResponseDto> createAlert(@PathVariable String consumerPublicId,
                                                        @RequestBody AlertRequestDto dto) {
        return ResponseEntity.ok(alertService.createAlertForConsumer(consumerPublicId, dto));
    }
}