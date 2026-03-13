package com.volttrack.volttrack.service;

import java.util.List;
import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;

public interface BillService {

    BillResponseDto createBill(BillRequestDto requestDto);

    List<BillResponseDto> getAllBills();

    BillResponseDto getBillById(Long id);

    void deleteBill(Long id);
}