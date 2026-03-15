package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.service.MeterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final MeterService meterService;

    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping
    public ResponseEntity<MeterResponseDto> createMeter(@Valid @RequestBody MeterRequestDto requestDto) {
        return ResponseEntity.ok(meterService.createMeter(requestDto));
    }

    @GetMapping
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterResponseDto> getMeterById(@PathVariable Long id) {
        return ResponseEntity.ok(meterService.getMeterById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long id) {
        meterService.deleteMeter(id);
        return ResponseEntity.noContent().build();
    }
}
