package com.volttrack.volttrack.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import com.volttrack.volttrack.entity.enums.BillStatus;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.entity.*;
import com.volttrack.volttrack.exception.BillingException;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.*;
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
    @Transactional
    @CacheEvict(value = {"allBills", "billsByConsumer"}, allEntries = true)
    public BillResponseDto createBill(BillRequestDto requestDto) {
        log.info("Creating bill for meterPublicId={}", requestDto.getMeterPublicId());

        Meter meter = meterRepository.findByPublicId(requestDto.getMeterPublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meter not found with publicId: " + requestDto.getMeterPublicId())
                );

        User consumer = meter.getUser();
        return generateBill(meter, consumer);
    }

    @Override
    @Cacheable(value = "allBills", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BillResponseDto> getAllBills(Pageable pageable) {
        return billRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Cacheable(value = "billsByPublicId", key = "#publicId")
    public BillResponseDto getBillByPublicId(String publicId) {
        Bill bill = billRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with publicId: " + publicId)
                );
        return toResponseDto(bill);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "billsByPublicId", key = "#publicId"),
            @CacheEvict(value = "allBills", allEntries = true),
            @CacheEvict(value = "billsByConsumer", allEntries = true)
    })
    public void deleteBillByPublicId(String publicId) {
        Bill bill = billRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with publicId: " + publicId)
                );
        billRepository.delete(bill);
        log.info("Bill deleted successfully with publicId={}", publicId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allBills", "billsByConsumer"}, allEntries = true)
    public BillResponseDto generateBillForConsumer(String consumerPublicId) {
        log.info("Generating bill for consumerPublicId={}", consumerPublicId);

        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId)
                );

        Meter meter = meterRepository.findByUser_Id(consumer.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No meter found for consumerPublicId: " + consumerPublicId)
                );

        return generateBill(meter, consumer);
    }

    @Override
    @Cacheable(value = "billsByConsumer", key = "#consumerPublicId + '_' + #pageable.pageNumber")
    public Page<BillResponseDto> getBillsByConsumer(String consumerPublicId, Pageable pageable) {
        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId)
                );

        return billRepository.findByConsumer_Id(consumer.getId(), pageable)
                .map(this::toResponseDto);
    }

    private BillResponseDto generateBill(Meter meter, User consumer) {
        MeterReading opening = meterReadingRepository
                .findTopByMeter_IdOrderByTimestampAsc(meter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No readings found for meter"));

        MeterReading closing = meterReadingRepository
                .findTopByMeter_IdOrderByTimestampDesc(meter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No readings found for meter"));

        Double openingReading = opening.getUnitsConsumed();
        Double closingReading = closing.getUnitsConsumed();

        if (closingReading < openingReading) {
            throw new BillingException("Closing reading cannot be less than opening reading");
        }

        Double unitsConsumed = closingReading - openingReading;
        Double baseAmount = unitsConsumed * 5.0;
        Double fixedCharges = 50.0;
        Double taxAmount = baseAmount * 0.1;
        Double totalAmount = baseAmount + fixedCharges + taxAmount;

        LocalDateTime now = LocalDateTime.now();
        String publicId = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Bill bill = Bill.builder()
                .publicId(publicId)
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
        return toResponseDto(saved);
    }

    private BillResponseDto toResponseDto(Bill bill) {
        return BillResponseDto.builder()
                .publicId(bill.getPublicId())
                .meterPublicId(bill.getMeter().getPublicId())
                .consumerPublicId(bill.getConsumer().getPublicId())
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