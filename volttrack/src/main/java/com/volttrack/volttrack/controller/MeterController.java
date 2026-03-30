package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.service.MeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    @Operation(summary = "Create meter", description = "Create a new meter. Accessible by ADMIN and OFFICER roles.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterResponseDto> createMeter(@Valid @RequestBody MeterRequestDto requestDto) {
        return ResponseEntity.ok(meterService.createMeter(requestDto));
    }

    // Add this to your MeterController.java

    @Operation(summary = "Assign meter to consumer", description = "Checks if meter exists, if not, creates one.")
    @PostMapping("/assign/{consumerPublicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<?> assignMeterToConsumer(
            @PathVariable String consumerPublicId,
            @Valid @RequestBody MeterRequestDto requestDto) {

        // We use a custom response or check in the service to return 409 if exists
        return ResponseEntity.ok(meterService.assignMeterToConsumer(consumerPublicId, requestDto));
    }

    @Operation(summary = "Get all meters", description = "Retrieve all meters (paginated).")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @Operation(summary = "Get meter by Public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or @meterSecurity.isOwner(#publicId, authentication)")
    public ResponseEntity<MeterResponseDto> getMeterByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(meterService.getMeterByPublicId(publicId));
    }

    @Operation(summary = "Delete meter by Public ID")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMeter(@PathVariable String publicId) {
        meterService.deleteMeterByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }
}