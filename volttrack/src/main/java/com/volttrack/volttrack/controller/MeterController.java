package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.service.MeterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final MeterService meterService;

    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterResponseDto> createMeter(@Valid @RequestBody MeterRequestDto requestDto) {
        return ResponseEntity.ok(meterService.createMeter(requestDto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @GetMapping("/{meterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #meterId == authentication.principal.id")
    public ResponseEntity<MeterResponseDto> getMeterById(@PathVariable Long meterId) {
        return ResponseEntity.ok(meterService.getMeterById(meterId));
    }

    @DeleteMapping("/{meterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long meterId) {
        meterService.deleteMeter(meterId);
        return ResponseEntity.noContent().build();
    }
}
