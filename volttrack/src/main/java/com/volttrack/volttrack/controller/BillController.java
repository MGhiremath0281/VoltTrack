package com.volttrack.volttrack.controller;

import java.util.List;

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
    public BillResponseDto createBill(@RequestBody BillRequestDto requestDto) {
        return billService.createBill(requestDto);
    }

    @GetMapping
    public List<BillResponseDto> getAllBills() {
        return billService.getAllBills();
    }

    @GetMapping("/{id}")
    public BillResponseDto getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
    }
}
