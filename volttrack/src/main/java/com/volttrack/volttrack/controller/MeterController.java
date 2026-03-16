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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;


    @Operation(summary = "Create meter", description = "Create a new meter. Accessible by ADMIN and OFFICER roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN or OFFICER role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<MeterResponseDto> createMeter(@Valid @RequestBody MeterRequestDto requestDto) {
        return ResponseEntity.ok(meterService.createMeter(requestDto));
    }

    @Operation(summary = "Get all meters", description = "Retrieve a paginated list of all meters. Accessible by ADMIN and OFFICER.")
    @ApiResponse(responseCode = "200", description = "List of meters retrieved successfully")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @Operation(summary = "Get meter by ID", description = "Retrieve a specific meter by ID. Accessible by ADMIN, OFFICER, or the meter owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter found"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{meterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or @meterSecurity.isOwner(#meterId, authentication)")
    public ResponseEntity<MeterResponseDto> getMeterById(@PathVariable Long meterId) {
        return ResponseEntity.ok(meterService.getMeterById(meterId));
    }

    @Operation(summary = "Delete meter", description = "Delete a meter by ID. Only ADMIN can delete meters.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Meter deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN role")
    })
    @DeleteMapping("/{meterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long meterId) {
        meterService.deleteMeter(meterId);
        return ResponseEntity.noContent().build();
    }
}
