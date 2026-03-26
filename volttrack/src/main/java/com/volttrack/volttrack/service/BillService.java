package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillService {


    BillResponseDto createBill(BillRequestDto requestDto);

    Page<BillResponseDto> getAllBills(Pageable pageable);

    BillResponseDto getBillByPublicId(String publicId);
    void deleteBillByPublicId(String publicId);
    List<BillResponseDto> getBillsByUserPublicId(String publicId);

    BillResponseDto createBillForUser(String meterPublicId, String userPublicId);

    BillResponseDto generateBillForConsumer(String consumerPublicId);
    Page<BillResponseDto> getBillsByConsumer(String consumerPublicId, Pageable pageable);
}