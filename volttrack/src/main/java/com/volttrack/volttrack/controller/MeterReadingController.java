package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterReadingDTO;
import com.volttrack.volttrack.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    @PostMapping
    public ResponseEntity<MeterReadingDTO> createReading(@RequestBody MeterReadingDTO dto) {
        MeterReadingDTO saved = meterReadingService.saveReading(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterReadingDTO> getReadingById(@PathVariable Long id) {
        MeterReadingDTO reading = meterReadingService.getReadingById(id);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<MeterReadingDTO>> getAllReadings() {
        List<MeterReadingDTO> readings = meterReadingService.getAllReadings();
        return ResponseEntity.ok(readings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Long id) {
        meterReadingService.deleteReading(id);
        return ResponseEntity.noContent().build();
    }
}
