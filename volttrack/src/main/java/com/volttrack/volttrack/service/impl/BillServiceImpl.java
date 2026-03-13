package com.volttrack.volttrack.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;

import com.volttrack.volttrack.entity.Bill;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.BillingCycle;
import com.volttrack.volttrack.entity.BillStatus;

import com.volttrack.volttrack.repository.BillRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;

import com.volttrack.volttrack.service.BillService;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    public BillServiceImpl(BillRepository billRepository,
                           MeterRepository meterRepository,
                           UserRepository userRepository) {
        this.billRepository = billRepository;
        this.meterRepository = meterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BillResponseDto createBill(BillRequestDto requestDto) {
        Meter meter = meterRepository.findById(requestDto.getMeterId())
                .orElseThrow(() -> new RuntimeException("Meter not found"));
        User consumer = userRepository.findById(requestDto.getConsumerId())
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        Bill bill = Bill.builder()
                .meter(meter)
                .consumer(consumer)
                .billingCycle(BillingCycle.valueOf(requestDto.getBillingCycle().toUpperCase()))
                .cycleStartDate(requestDto.getCycleStartDate())
                .cycleEndDate(requestDto.getCycleEndDate())
                .openingReading(requestDto.getOpeningReading())
                .closingReading(requestDto.getClosingReading())
                .unitsConsumed(requestDto.getUnitsConsumed())
                .baseAmount(requestDto.getBaseAmount())
                .fixedCharges(requestDto.getFixedCharges())
                .taxAmount(requestDto.getTaxAmount())
                .totalAmount(requestDto.getTotalAmount())
                .status(BillStatus.valueOf(requestDto.getStatus().toUpperCase()))
                .generatedAt(requestDto.getGeneratedAt())
                .dueDate(requestDto.getDueDate())
                .build();

        Bill saved = billRepository.save(bill);
        return toResponseDto(saved);
    }

    @Override
    public List<BillResponseDto> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BillResponseDto getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        return toResponseDto(bill);
    }

    @Override
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    private BillResponseDto toResponseDto(Bill bill) {
        return BillResponseDto.builder()
                .id(bill.getId())
                .meterId(bill.getMeter().getId())
                .consumerId(bill.getConsumer().getId())
                .billingCycle(bill.getBillingCycle().name())
                .cycleStartDate(bill.getCycleStartDate())
                .cycleEndDate(bill.getCycleEndDate())
                .openingReading(bill.getOpeningReading())
                .closingReading(bill.getClosingReading())
                .unitsConsumed(bill.getUnitsConsumed())
                .baseAmount(bill.getBaseAmount())
                .fixedCharges(bill.getFixedCharges())
                .taxAmount(bill.getTaxAmount())
                .totalAmount(bill.getTotalAmount())
                .status(bill.getStatus().name())
                .generatedAt(bill.getGeneratedAt())
                .dueDate(bill.getDueDate())
                .build();
    }
}
