package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillService {
    BillResponseDto createBill(BillRequestDto requestDto);
    Page<BillResponseDto> getAllBills(Pageable pageable);
    BillResponseDto getBillById(Long id);
    void deleteBill(Long id);
    BillResponseDto generateBillForConsumer(String consumerPublicId);
    Page<BillResponseDto> getBillsByConsumer(String consumerPublicId, Pageable pageable);
}
