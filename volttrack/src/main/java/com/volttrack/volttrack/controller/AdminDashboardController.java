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

    @Operation(summary = "Get user by ID", description = "Retrieve details of a specific user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Approve officer", description = "Admin approves an officer by setting active=true")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Officer approved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - only ADMIN can approve"),
            @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    @PutMapping("/users/{id}/approve")
    public ResponseEntity<UserResponseDto> approveOfficer(@PathVariable Long id) {
        UserResponseDto approved = userService.approveOfficer(id);
        return ResponseEntity.ok(approved);
    }

    @Operation(summary = "Get all meters", description = "Retrieve a paginated list of all meters")
    @GetMapping("/meters")
    public ResponseEntity<Page<MeterResponseDto>> getAllMeters(Pageable pageable) {
        return ResponseEntity.ok(meterService.getAllMeters(pageable));
    }

    @Operation(summary = "Get meter by ID", description = "Retrieve details of a specific meter by its ID")
    @GetMapping("/meters/{id}")
    public ResponseEntity<MeterResponseDto> getMeterById(@PathVariable Long id) {
        return ResponseEntity.ok(meterService.getMeterById(id));
    }

    @Operation(summary = "Delete meter", description = "Delete a meter by its ID")
    @DeleteMapping("/meters/{id}")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long id) {
        meterService.deleteMeter(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all bills", description = "Retrieve a paginated list of all bills")
    @GetMapping("/bills")
    public ResponseEntity<Page<BillResponseDto>> getAllBills(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllBills(pageable));
    }

    @Operation(summary = "Get bill by ID", description = "Retrieve details of a specific bill by its ID")
    @GetMapping("/bills/{id}")
    public ResponseEntity<BillResponseDto> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @Operation(summary = "Delete bill", description = "Delete a bill by its ID")
    @DeleteMapping("/bills/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all alerts", description = "Retrieve a paginated list of all alerts")
    @GetMapping("/alerts")
    public ResponseEntity<Page<AlertResponseDto>> getAllAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @Operation(summary = "Get alert by ID", description = "Retrieve details of a specific alert by its ID")
    @GetMapping("/alerts/{id}")
    public ResponseEntity<AlertResponseDto> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @Operation(summary = "Delete alert", description = "Delete an alert by its ID")
    @DeleteMapping("/alerts/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}
