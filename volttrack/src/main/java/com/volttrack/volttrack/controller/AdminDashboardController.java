package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.dto.alert.AlertResponseDto;
import com.volttrack.volttrack.service.UserService;
import com.volttrack.volttrack.service.MeterService;
import com.volttrack.volttrack.service.BillService;
import com.volttrack.volttrack.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final MeterService meterService;
    private final BillService billService;
    private final AlertService alertService;

    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all registered users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by publicId", description = "Retrieve details of a specific user by their publicId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{publicId}")
    public ResponseEntity<UserResponseDto> getUserByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(userService.getUserByPublicId(publicId));
    }

    @Operation(summary = "Delete user", description = "Delete a user by their publicId")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{publicId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String publicId) {
        userService.deleteUserByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Approve officer", description = "Admin approves an officer by setting active=true")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Officer approved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - only ADMIN can approve"),
            @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    @PutMapping("/users/{publicId}/approve")
    public ResponseEntity<UserResponseDto> approveOfficer(@PathVariable String publicId) {
        UserResponseDto approved = userService.approveOfficer(publicId);
        return ResponseEntity.ok(approved);
    }

    @Operation(summary = "Get all meters", description = "Retrieve a paginated list of all meters")
    @GetMapping("/meters")
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @Operation(summary = "Get meter by publicId", description = "Retrieve details of a specific meter by its publicId")
    @GetMapping("/meters/{publicId}")
    public ResponseEntity<MeterResponseDto> getMeterByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(meterService.getMeterByPublicId(publicId));
    }

    @Operation(summary = "Delete meter", description = "Delete a meter by its publicId")
    @DeleteMapping("/meters/{publicId}")
    public ResponseEntity<Void> deleteMeter(@PathVariable String publicId) {
        meterService.deleteMeterByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all bills", description = "Retrieve a paginated list of all bills")
    @GetMapping("/bills")
    public ResponseEntity<Page<BillResponseDto>> getAllBills(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllBills(pageable));
    }

    @Operation(summary = "Get bill by publicId", description = "Retrieve details of a specific bill by its publicId")
    @GetMapping("/bills/{publicId}")
    public ResponseEntity<BillResponseDto> getBillByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(billService.getBillByPublicId(publicId));
    }

    @Operation(summary = "Delete bill", description = "Delete a bill by its publicId")
    @DeleteMapping("/bills/{publicId}")
    public ResponseEntity<Void> deleteBill(@PathVariable String publicId) {
        billService.deleteBillByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all alerts", description = "Retrieve a paginated list of all alerts")
    @GetMapping("/alerts")
    public ResponseEntity<Page<AlertResponseDto>> getAllAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @Operation(summary = "Get alert by publicId", description = "Retrieve details of a specific alert by its publicId")
    @GetMapping("/alerts/{publicId}")
    public ResponseEntity<AlertResponseDto> getAlertByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(alertService.getAlertByPublicId(publicId));
    }

    @Operation(summary = "Delete alert", description = "Delete an alert by its publicId")
    @DeleteMapping("/alerts/{publicId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable String publicId) {
        alertService.deleteAlertByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }
}
