package com.volttrack.volttrack.service;

import java.util.List;
import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillService {
    BillResponseDto createBill(BillRequestDto requestDto);
    Page<BillResponseDto> getAllBills(Pageable pageable);
    BillResponseDto getBillById(Long id);
    void deleteBill(Long id);
    BillResponseDto generateBillForConsumer(Long consumerId);
    Page<BillResponseDto> getBillsByConsumer(Long consumerId, Pageable pageable);
}
