package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillService {
    BillResponseDto createBill(BillRequestDto requestDto);
    Page<BillResponseDto> getAllBills(Pageable pageable);

    // Legacy numeric ID methods (still useful internally)
    BillResponseDto getBillById(Long id);
    void deleteBill(Long id);

    BillResponseDto getBillByPublicId(String publicId);
    void deleteBillByPublicId(String publicId);

    // Consumer‑specific methods using publicId
    BillResponseDto generateBillForConsumer(String consumerPublicId);
    Page<BillResponseDto> getBillsByConsumer(String consumerPublicId, Pageable pageable);
}
