package com.volttrack.volttrack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.service.BillService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<BillResponseDto> createBill(@Valid @RequestBody BillRequestDto requestDto) {
        BillResponseDto saved = billService.createBill(requestDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<BillResponseDto>> getAllBills(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllBills(pageable));
    }

    @GetMapping("/{billId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER') or #billId == authentication.principal.id")
    public ResponseEntity<BillResponseDto> getBillById(@PathVariable Long billId) {
        return ResponseEntity.ok(billService.getBillById(billId));
    }

    @DeleteMapping("/{billId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBill(@PathVariable Long billId) {
        billService.deleteBill(billId);
        return ResponseEntity.noContent().build();
    }
}
