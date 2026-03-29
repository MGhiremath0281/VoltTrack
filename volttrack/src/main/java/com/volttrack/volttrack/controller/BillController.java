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
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    /**
     * 🔥 ADMIN: Generate bill for a specific meter (monthly, idempotent)
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<BillResponseDto> generateBill(@Valid @RequestBody BillRequestDto requestDto) {
        return ResponseEntity.ok(billService.createBill(requestDto));
    }

    /**
     * 🔥 ADMIN: Generate bill for a specific consumer
     */
    @PostMapping("/generate/consumer/{consumerPublicId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    public ResponseEntity<BillResponseDto> generateBillForConsumer(@PathVariable String consumerPublicId) {
        return ResponseEntity.ok(billService.generateBillForConsumer(consumerPublicId));
    }

    /**
     * Get all bills (Admin/Officer)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<Page<BillResponseDto>> getAllBills(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllBills(pageable));
    }

    /**
     * Get bill by public ID
     */
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<BillResponseDto> getBillByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(billService.getBillByPublicId(publicId));
    }

    /**
     * Delete bill (ADMIN only)
     */
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBill(@PathVariable String publicId) {
        billService.deleteBillByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }
}