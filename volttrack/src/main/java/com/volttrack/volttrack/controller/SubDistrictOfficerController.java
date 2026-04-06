package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.dto.report.ConsumptionReportDto;
import com.volttrack.volttrack.dto.report.RevenueReportDto;
import com.volttrack.volttrack.dto.report.OfficerPerformanceDto;
import com.volttrack.volttrack.dto.report.CustomerSegmentationDto;
import com.volttrack.volttrack.service.UserService;
import com.volttrack.volttrack.service.ReportService;
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
@RequestMapping("/api/subdistrict/dashboard")
@PreAuthorize("hasRole('SUB_DISTRICT_OFFICER')")
@RequiredArgsConstructor
public class SubDistrictOfficerController {

    private final UserService userService;
    private final ReportService reportService;

    @Operation(summary = "Get all pending officers", description = "Retrieve a paginated list of officers awaiting approval in this sub-district")
    @ApiResponse(responseCode = "200", description = "List of pending officers retrieved successfully")
    @GetMapping("/officers/pending")
    public ResponseEntity<Page<UserResponseDto>> getPendingOfficers(Pageable pageable) {
        return ResponseEntity.ok(userService.getPendingOfficers(pageable));
    }

    @Operation(summary = "Approve officer", description = "Sub-district officer approves an officer registration")
    @PutMapping("/officers/{publicId}/approve")
    public ResponseEntity<UserResponseDto> approveOfficer(@PathVariable String publicId,
                                                          @RequestParam Long approverId) {
        return ResponseEntity.ok(userService.approveOfficerBySubDistrict(publicId, approverId));
    }

    @Operation(summary = "Reject officer", description = "Sub-district officer rejects an officer registration")
    @PutMapping("/officers/{publicId}/reject")
    public ResponseEntity<UserResponseDto> rejectOfficer(@PathVariable String publicId,
                                                         @RequestParam Long approverId) {
        return ResponseEntity.ok(userService.rejectOfficerBySubDistrict(publicId, approverId));
    }

    @Operation(summary = "Get officers in sub-district", description = "Retrieve a paginated list of all officers managed by this sub-district officer")
    @GetMapping("/officers")
    public ResponseEntity<Page<UserResponseDto>> getAllOfficers(@RequestParam Long subDistrictOfficerId,
                                                                Pageable pageable) {
        return ResponseEntity.ok(userService.getOfficersInSubDistrict(subDistrictOfficerId, pageable));
    }

    @Operation(summary = "Suspend officer", description = "Sub-district officer suspends an officer in their sub-district")
    @PutMapping("/officers/{publicId}/suspend")
    public ResponseEntity<UserResponseDto> suspendOfficer(@PathVariable String publicId,
                                                          @RequestParam Long approverId) {
        return ResponseEntity.ok(userService.suspendOfficer(publicId, approverId));
    }

    @Operation(summary = "Get sub-district customer report", description = "Retrieve a paginated list of consumers linked to officers in this sub-district")
    @GetMapping("/reports/customers")
    public ResponseEntity<Page<UserResponseDto>> getSubDistrictCustomerReport(@RequestParam Long subDistrictOfficerId,
                                                                              Pageable pageable) {
        return ResponseEntity.ok(userService.getSubDistrictCustomerReport(subDistrictOfficerId, pageable));
    }

    @Operation(summary = "Consumption trends", description = "Retrieve aggregated consumption data for visualization")
    @GetMapping("/reports/consumption")
    public ResponseEntity<ConsumptionReportDto> getConsumptionReport(@RequestParam Long subDistrictOfficerId,
                                                                     @RequestParam String range) {
        return ResponseEntity.ok(reportService.getConsumptionReport(subDistrictOfficerId, range));
    }

    @Operation(summary = "Revenue collection", description = "Retrieve aggregated revenue data for visualization")
    @GetMapping("/reports/revenue")
    public ResponseEntity<RevenueReportDto> getRevenueReport(@RequestParam Long subDistrictOfficerId,
                                                             @RequestParam String range) {
        return ResponseEntity.ok(reportService.getRevenueReport(subDistrictOfficerId, range));
    }

    @Operation(summary = "Officer performance", description = "Retrieve officer performance metrics for visualization")
    @GetMapping("/reports/officer-performance")
    public ResponseEntity<OfficerPerformanceDto> getOfficerPerformance(@RequestParam Long subDistrictOfficerId,
                                                                       Pageable pageable) {
        return ResponseEntity.ok(reportService.getOfficerPerformance(subDistrictOfficerId, pageable));
    }

    @Operation(summary = "Customer segmentation", description = "Retrieve customer segmentation data for visualization")
    @GetMapping("/reports/customer-segmentation")
    public ResponseEntity<CustomerSegmentationDto> getCustomerSegmentation(@RequestParam Long subDistrictOfficerId) {
        return ResponseEntity.ok(reportService.getCustomerSegmentation(subDistrictOfficerId));
    }
}
