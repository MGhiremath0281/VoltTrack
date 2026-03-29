package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.service.UserService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.MeterReadingService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.security.CustomUserDetails;

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

    private String getPublicId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getPublicId();
        }

        throw new RuntimeException("Invalid user principal");
    }

    /**
     * Get consumer profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getProfile(Authentication authentication) {
        String publicId = getPublicId(authentication);
        return ResponseEntity.ok(userService.getUserByPublicId(publicId));
    }

    /**
     * Get all meters
     */
    @GetMapping("/meters")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeterResponseDto>> getMeters(Authentication authentication) {
        String publicId = getPublicId(authentication);
        return ResponseEntity.ok(meterService.getMetersByUserPublicId(publicId));
    }

    /**
     * Get all readings
     */
    @GetMapping("/readings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeterReadingDTO>> getReadings(Authentication authentication) {
        String publicId = getPublicId(authentication);
        return ResponseEntity.ok(meterReadingService.getReadingsByUserPublicId(publicId));
    }

    /**
     * Create reading + WebSocket broadcast
     */
    @PostMapping("/readings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeterReadingDTO> createReading(
            @Valid @RequestBody MeterReadingDTO dto,
            Authentication authentication) {

        String publicId = getPublicId(authentication);

        MeterReadingDTO saved = meterReadingService.saveReadingForUser(dto, publicId);

        messagingTemplate.convertAndSend("/topic/meter-readings", saved);

        return ResponseEntity.ok(saved);
    }

    /**
     * Get ALL bills (history)
     */
    @GetMapping("/bills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BillResponseDto>> getBills(Authentication authentication) {
        String publicId = getPublicId(authentication);
        return ResponseEntity.ok(billService.getBillsByUserPublicId(publicId));
    }

    /**
     * Get LATEST bill (recommended for UI)
     */
    @GetMapping("/bills/latest")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BillResponseDto> getLatestBill(Authentication authentication) {
        String publicId = getPublicId(authentication);

        List<BillResponseDto> bills = billService.getBillsByUserPublicId(publicId);

        if (bills.isEmpty()) {
            throw new RuntimeException("No bills found");
        }

        return ResponseEntity.ok(bills.get(bills.size() - 1));
    }
}