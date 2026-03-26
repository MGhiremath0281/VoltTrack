package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.service.MeterReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private final SimpMessagingTemplate messagingTemplate; 

    @PostMapping
    public ResponseEntity<MeterReadingDTO> createReading(@Valid @RequestBody MeterReadingDTO dto) {
        MeterReadingDTO saved = meterReadingService.saveReading(dto);

        // Broadcast to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/meter-readings", saved);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{readingPublicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterReadingDTO> getReadingByPublicId(@PathVariable String readingPublicId) {
        return ResponseEntity.ok(meterReadingService.getReadingByPublicId(readingPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterReadingDTO>> getAllReadings(Pageable pageable) {
        return ResponseEntity.ok(meterReadingService.getAllReadings(pageable));
    }

    @DeleteMapping("/{readingPublicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReading(@PathVariable String readingPublicId) {
        meterReadingService.deleteReadingByPublicId(readingPublicId);
        return ResponseEntity.noContent().build();
    }
}