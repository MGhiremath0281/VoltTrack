package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.service.MeterReadingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterReadingDTO> createReading(@Valid @RequestBody MeterReadingDTO dto) {
        MeterReadingDTO saved = meterReadingService.saveReading(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{readingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #readingId == authentication.principal.id")
    public ResponseEntity<MeterReadingDTO> getReadingById(@PathVariable Long readingId) {
        MeterReadingDTO reading = meterReadingService.getReadingById(readingId);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterReadingDTO>> getAllReadings(Pageable pageable) {
        return ResponseEntity.ok(meterReadingService.getAllReadings(pageable));
    }

    @DeleteMapping("/{readingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReading(@PathVariable Long readingId) {
        meterReadingService.deleteReading(readingId);
        return ResponseEntity.noContent().build();
    }
}
