package com.volttrack.volttrack.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Operation(summary = "Create bill", description = "Create a new bill. Accessible by ADMIN and OFFICER roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN or OFFICER role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<BillResponseDto> createBill(@Valid @RequestBody BillRequestDto requestDto) {
        BillResponseDto saved = billService.createBill(requestDto);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Get all bills", description = "Retrieve a paginated list of all bills. Accessible by ADMIN and OFFICER.")
    @ApiResponse(responseCode = "200", description = "List of bills retrieved successfully")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<BillResponseDto>> getAllBills(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllBills(pageable));
    }

    @Operation(summary = "Get bill by ID", description = "Retrieve a specific bill by ID. Accessible by ADMIN, OFFICER, or the bill owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill found"),
            @ApiResponse(responseCode = "404", description = "Bill not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{billId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #billId == authentication.principal.id")
    public ResponseEntity<BillResponseDto> getBillById(@PathVariable Long billId) {
        return ResponseEntity.ok(billService.getBillById(billId));
    }

    @Operation(summary = "Delete bill", description = "Delete a bill by ID. Only ADMIN can delete bills.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bill deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Bill not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - requires ADMIN role")
    })
    @DeleteMapping("/{billId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBill(@PathVariable Long billId) {
        billService.deleteBill(billId);
        return ResponseEntity.noContent().build();
    }
}
