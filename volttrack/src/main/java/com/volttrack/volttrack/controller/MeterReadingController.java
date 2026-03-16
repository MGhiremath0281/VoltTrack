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

    @Operation(summary = "Create meter reading", description = "Create a new meter reading. Accessible by ADMIN and OFFICER roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter reading created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN or OFFICER role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterReadingDTO> createReading(@Valid @RequestBody MeterReadingDTO dto) {
        MeterReadingDTO saved = meterReadingService.saveReading(dto);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Get meter reading by ID", description = "Retrieve a specific meter reading by ID. Accessible by ADMIN, OFFICER, or the reading owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter reading found"),
            @ApiResponse(responseCode = "404", description = "Meter reading not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{readingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #readingId == authentication.principal.id")
    public ResponseEntity<MeterReadingDTO> getReadingById(@PathVariable Long readingId) {
        MeterReadingDTO reading = meterReadingService.getReadingById(readingId);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all meter readings", description = "Retrieve a paginated list of all meter readings. Accessible by ADMIN and OFFICER.")
    @ApiResponse(responseCode = "200", description = "List of meter readings retrieved successfully")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterReadingDTO>> getAllReadings(Pageable pageable) {
        return ResponseEntity.ok(meterReadingService.getAllReadings(pageable));
    }

    @Operation(summary = "Delete meter reading", description = "Delete a meter reading by ID. Only ADMIN can delete readings.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Meter reading deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Meter reading not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN role")
    })
    @DeleteMapping("/{readingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReading(@PathVariable Long readingId) {
        meterReadingService.deleteReading(readingId);
        return ResponseEntity.noContent().build();
    }
}
