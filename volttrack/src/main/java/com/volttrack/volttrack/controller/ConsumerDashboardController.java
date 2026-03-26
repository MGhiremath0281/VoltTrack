package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.service.UserService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.MeterReadingService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ConsumerDashboardController {

    private final UserService userService;
    private final MeterService meterService;
    private final MeterReadingService meterReadingService;
    private final BillService billService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Get consumer profile (name, publicId, etc.)
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getProfile(Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        return ResponseEntity.ok(userService.getUserByPublicId(publicId));
    }

    /**
     * Get all meters for authenticated consumer
     */
    @GetMapping("/meters")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeterResponseDto>> getMeters(Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        return ResponseEntity.ok(meterService.getMetersByUserPublicId(publicId));
    }

    /**
     * Get all readings for consumer’s meters
     */
    @GetMapping("/readings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeterReadingDTO>> getReadings(Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        return ResponseEntity.ok(meterReadingService.getReadingsByUserPublicId(publicId));
    }

    /**
     * Create a new meter reading and broadcast via WebSocket
     */
    @PostMapping("/readings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeterReadingDTO> createReading(@Valid @RequestBody MeterReadingDTO dto,
                                                         Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        // Ownership check inside service layer
        MeterReadingDTO saved = meterReadingService.saveReadingForUser(dto, publicId);

        // Broadcast to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/meter-readings", saved);

        return ResponseEntity.ok(saved);
    }

    /**
     * Get bills for consumer’s meters
     */
    @GetMapping("/bills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BillResponseDto>> getBills(Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        return ResponseEntity.ok(billService.getBillsByUserPublicId(publicId));
    }

    /**
     * Generate a new bill for consumer’s meter
     */
    @PostMapping("/bills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BillResponseDto> createBill(@Valid @RequestBody MeterResponseDto meterDto,
                                                      Authentication authentication) {
        String publicId = ((CustomUserPrincipal) authentication.getPrincipal()).getPublicId();
        return ResponseEntity.ok(billService.createBillForUser(meterDto.getPublicId(), publicId));
    }
}
