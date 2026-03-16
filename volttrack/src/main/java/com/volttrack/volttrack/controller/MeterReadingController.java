package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.service.MeterReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterReadingDTO> createReading(@Valid @RequestBody MeterReadingDTO dto) {
        MeterReadingDTO saved = meterReadingService.saveReading(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{readingPublicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #readingPublicId == authentication.principal.publicId")
    public ResponseEntity<MeterReadingDTO> getReadingByPublicId(@PathVariable String readingPublicId) {
        MeterReadingDTO reading = meterReadingService.getReadingByPublicId(readingPublicId);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
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
