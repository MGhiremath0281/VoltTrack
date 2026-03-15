package com.volttrack.volttrack.service.impl;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.entity.Bill;
import com.volttrack.volttrack.entity.BillStatus;
import com.volttrack.volttrack.entity.BillingCycle;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.exception.BillingException;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.BillRepository;
import com.volttrack.volttrack.repository.MeterReadingRepository;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.BillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;
    private final MeterReadingRepository meterReadingRepository;

    public BillServiceImpl(BillRepository billRepository,
                           MeterRepository meterRepository,
                           UserRepository userRepository,
                           MeterReadingRepository meterReadingRepository) {
        this.billRepository = billRepository;
        this.meterRepository = meterRepository;
        this.userRepository = userRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    public BillResponseDto createBill(BillRequestDto requestDto) {
        log.info("Creating bill for meterId={}", requestDto.getMeterId());

        Meter meter = meterRepository.findById(requestDto.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + requestDto.getMeterId()));
        User consumer = meter.getUser();

        return generateBill(meter, consumer);
    }

    @Override
    public Page<BillResponseDto> getAllBills(Pageable pageable) {
        log.debug("Fetching all bills with pagination");
        return billRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    public BillResponseDto getBillById(Long id) {
        log.info("Fetching bill with id={}", id);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        return toResponseDto(bill);
    }

    @Override
    public void deleteBill(Long id) {
        log.info("Deleting bill with id={}", id);
        if (!billRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Bill not found with id: " + id);
        }
        billRepository.deleteById(id);
        log.info("Bill deleted successfully with id={}", id);
    }

    @Override
    public BillResponseDto generateBillForConsumer(Long consumerId) {
        log.info("Generating bill for consumerId={}", consumerId);

        User consumer = userRepository.findById(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with id: " + consumerId));

        Meter meter = meterRepository.findByUser_Id(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("No meter found for consumer id: " + consumerId));

        return generateBill(meter, consumer);
    }

    @Override
    public Page<BillResponseDto> getBillsByConsumer(Long consumerId, Pageable pageable) {
        log.debug("Fetching bills for consumerId={}", consumerId);
        return billRepository.findByConsumer_Id(consumerId, pageable).map(this::toResponseDto);
    }

    private BillResponseDto generateBill(Meter meter, User consumer) {
        MeterReading opening = meterReadingRepository
                .findTopByMeter_IdOrderByTimestampAsc(meter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No readings found for meter id: " + meter.getId()));

        MeterReading closing = meterReadingRepository
                .findTopByMeter_IdOrderByTimestampDesc(meter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No readings found for meter id: " + meter.getId()));

        Double openingReading = opening.getUnitsConsumed();
        Double closingReading = closing.getUnitsConsumed();

        if (closingReading < openingReading) {
            throw new BillingException("Closing reading cannot be less than opening reading for meter id: " + meter.getId());
        }

        Double unitsConsumed = closingReading - openingReading;
        Double baseAmount = unitsConsumed * 5.0;
        Double fixedCharges = 50.0;
        Double taxAmount = baseAmount * 0.1;
        Double totalAmount = baseAmount + fixedCharges + taxAmount;

        LocalDateTime now = LocalDateTime.now();

        Bill bill = Bill.builder()
                .meter(meter)
                .consumer(consumer)
                .billingCycle(BillingCycle.MONTHLY)
                .cycleStartDate(now.withDayOfMonth(1))
                .cycleEndDate(now.withDayOfMonth(now.toLocalDate().lengthOfMonth()))
                .openingReading(openingReading)
                .closingReading(closingReading)
                .unitsConsumed(unitsConsumed)
                .baseAmount(baseAmount)
                .fixedCharges(fixedCharges)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .status(BillStatus.UNPAID)
                .generatedAt(now)
                .dueDate(now.plusDays(15))
                .build();

        Bill saved = billRepository.save(bill);
        log.info("Bill generated successfully with id={} for consumerId={}", saved.getId(), consumer.getId());

        return toResponseDto(saved);
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
