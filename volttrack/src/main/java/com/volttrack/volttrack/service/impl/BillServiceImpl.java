package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.entity.*;
import com.volttrack.volttrack.entity.enums.BillStatus;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.exception.BillingException;
import com.volttrack.volttrack.exception.ResourceNotFoundException;
import com.volttrack.volttrack.repository.*;
import com.volttrack.volttrack.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with publicId: " + requestDto.getMeterPublicId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with publicId: " + publicId));
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
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with publicId: " + publicId));
        billRepository.delete(bill);
        log.info("Bill deleted successfully with publicId={}", publicId);
    }

    @Override
    public List<BillResponseDto> getBillsByUserPublicId(String publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Bill> bills = billRepository.findByConsumer_Id(user.getId(), Pageable.unpaged()).getContent();

        return bills.stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allBills", "billsByConsumer"}, allEntries = true)
    public BillResponseDto generateBillForConsumer(String consumerPublicId) {
        log.info("Generating bill for consumerPublicId={}", consumerPublicId);

        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId));

        Meter meter = meterRepository.findByUser_Id(consumer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No meter found for consumerPublicId: " + consumerPublicId));

        return generateBill(meter, consumer);
    }

    @Override
    @Cacheable(value = "billsByConsumer", key = "#consumerPublicId + '_' + #pageable.pageNumber")
    public Page<BillResponseDto> getBillsByConsumer(String consumerPublicId, Pageable pageable) {
        User consumer = userRepository.findByPublicId(consumerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with publicId: " + consumerPublicId));

        return billRepository.findByConsumer_Id(consumer.getId(), pageable)
                .map(this::toResponseDto);
    }

    private BillResponseDto generateBill(Meter meter, User consumer) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Calculate Billing Cycle Dates (Current Month)
        LocalDateTime start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59);

        // 2. Prevent Duplicate Billing for same cycle
        Optional<Bill> existing = billRepository.findByMeter_IdAndCycleStartDateAndCycleEndDate(meter.getId(), start, end);
        if (existing.isPresent()) {
            log.warn("Bill already exists for meter {} in the current cycle", meter.getPublicId());
            return toResponseDto(existing.get());
        }

        // 3. Determine Opening Reading (Either from last bill's closing or the first ever reading)
        double openingReadingValue;
        Optional<Bill> lastBill = billRepository.findTopByMeter_IdOrderByCycleEndDateDesc(meter.getId());

        if (lastBill.isPresent()) {
            openingReadingValue = lastBill.get().getClosingReading();
        } else {
            openingReadingValue = meterReadingRepository.findTopByMeter_IdOrderByTimestampAsc(meter.getId())
                    .map(MeterReading::getUnitsConsumed)
                    .orElseThrow(() -> new BillingException("No initial reading found"));
        }

        // 4. Get Latest Closing Reading
        MeterReading latestReading = meterReadingRepository.findTopByMeter_IdOrderByTimestampDesc(meter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No readings found for meter: " + meter.getPublicId()));

        double closingReadingValue = latestReading.getUnitsConsumed();

        if (closingReadingValue < openingReadingValue) {
            throw new BillingException("Closing reading (" + closingReadingValue + ") cannot be less than opening reading (" + openingReadingValue + ")");
        }

        // 5. Calculate Charges
        double unitsConsumed = closingReadingValue - openingReadingValue;
        double baseAmount = unitsConsumed * 5.0; // Rate: 5.0 per unit
        double fixedCharges = 50.0;
        double taxAmount = baseAmount * 0.1; // 10% tax
        double totalAmount = baseAmount + fixedCharges + taxAmount;

        String publicId = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 6. Build and Save Bill
        Bill bill = Bill.builder()
                .publicId(publicId)
                .meter(meter)
                .consumer(consumer)
                .billingCycle(BillingCycle.MONTHLY)
                .cycleStartDate(start)
                .cycleEndDate(end)
                .openingReading(openingReadingValue)
                .closingReading(closingReadingValue)
                .unitsConsumed(unitsConsumed)
                .baseAmount(baseAmount)
                .fixedCharges(fixedCharges)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .status(BillStatus.UNPAID)
                .generatedAt(now)
                .dueDate(end.plusDays(15))
                .build();

        Bill saved = billRepository.save(bill);
        log.info("Bill generated successfully with publicId={}", publicId);
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
    
    // Note: I consolidated 'createBillForUser' into the 'generateBill' flow 
    // to maintain consistency and DRY principles.
    @Override
    @Transactional
    public BillResponseDto createBillForUser(String meterPublicId, String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Meter meter = meterRepository.findByPublicId(meterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));

        if (!meter.getUser().getId().equals(user.getId())) {
            throw new BillingException("Unauthorized: Meter does not belong to user");
        }

        return generateBill(meter, user);
    }
}