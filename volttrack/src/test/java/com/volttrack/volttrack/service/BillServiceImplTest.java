package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.bill.BillRequestDto;
import com.volttrack.volttrack.dto.bill.BillResponseDto;
import com.volttrack.volttrack.entity.*;
import com.volttrack.volttrack.entity.enums.BillStatus;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.exception.BillingException;
import com.volttrack.volttrack.repository.*;
import com.volttrack.volttrack.service.impl.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock private BillRepository billRepository;
    @Mock private MeterRepository meterRepository;
    @Mock private UserRepository userRepository;
    @Mock private MeterReadingRepository meterReadingRepository;

    @InjectMocks private BillServiceImpl billService;

    private User sampleUser;
    private Meter sampleMeter;
    private MeterReading openingReading;
    private MeterReading closingReading;
    private Bill sampleBill;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).publicId("CON-1").username("testuser").build();

        sampleMeter = Meter.builder().id(10L).publicId("MET-100").user(sampleUser).build();

        openingReading = MeterReading.builder()
                .id(1L).unitsConsumed(100.0).timestamp(LocalDateTime.now().minusDays(30)).build();

        closingReading = MeterReading.builder()
                .id(2L).unitsConsumed(250.0).timestamp(LocalDateTime.now()).build();

        sampleBill = Bill.builder()
                .publicId("BILL-12345")
                .meter(sampleMeter)
                .consumer(sampleUser)
                .billingCycle(BillingCycle.MONTHLY)
                .openingReading(100.0)
                .closingReading(250.0)
                .unitsConsumed(150.0)
                .baseAmount(750.0)
                .fixedCharges(50.0)
                .taxAmount(75.0)
                .totalAmount(875.0)
                .status(BillStatus.UNPAID)
                .build();
    }

    @Test
    @DisplayName("createBill: Should calculate and save bill correctly")
    void testCreateBill_Success() {
        BillRequestDto request = new BillRequestDto("MET-100");

        when(meterRepository.findByPublicId("MET-100")).thenReturn(Optional.of(sampleMeter));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampAsc(10L)).thenReturn(Optional.of(openingReading));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampDesc(10L)).thenReturn(Optional.of(closingReading));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> {
            Bill b = i.getArgument(0);
            b.setPublicId("BILL-GENERATED");
            return b;
        });

        BillResponseDto response = billService.createBill(request);

        assertNotNull(response);
        assertEquals(150.0, response.getUnitsConsumed()); // 250 - 100
        assertEquals(875.0, response.getTotalAmount());   // (150*5) + 50 + tax
        assertEquals("BILL-GENERATED", response.getPublicId());
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    @DisplayName("generateBill: Should throw BillingException if readings are invalid")
    void testGenerateBill_InvalidReadings() {
        // Swap readings so closing is less than opening
        closingReading.setUnitsConsumed(50.0);

        when(meterRepository.findByPublicId("MET-100")).thenReturn(Optional.of(sampleMeter));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampAsc(10L)).thenReturn(Optional.of(openingReading));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampDesc(10L)).thenReturn(Optional.of(closingReading));

        BillRequestDto request = new BillRequestDto("MET-100");

        assertThrows(BillingException.class, () -> billService.createBill(request));
    }

    @Test
    @DisplayName("generateBillForConsumer: Should find meter by user ID and generate bill")
    void testGenerateBillForConsumer_Success() {
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));
        when(meterRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleMeter));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampAsc(10L)).thenReturn(Optional.of(openingReading));
        when(meterReadingRepository.findTopByMeter_IdOrderByTimestampDesc(10L)).thenReturn(Optional.of(closingReading));
        when(billRepository.save(any(Bill.class))).thenReturn(sampleBill);

        BillResponseDto response = billService.generateBillForConsumer("CON-1");

        assertNotNull(response);
        assertEquals("CON-1", response.getConsumerPublicId());
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    @DisplayName("getBillsByConsumer: Should return paginated bills for consumer")
    void testGetBillsByConsumer() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByPublicId("CON-1")).thenReturn(Optional.of(sampleUser));
        when(billRepository.findByConsumer_Id(1L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(sampleBill)));

        Page<BillResponseDto> result = billService.getBillsByConsumer("CON-1", pageable);

        assertFalse(result.isEmpty());
        assertEquals("BILL-12345", result.getContent().get(0).getPublicId());
    }

    @Test
    @DisplayName("getBillByPublicId: Should return bill DTO")
    void testGetBillByPublicId_Success() {
        when(billRepository.findByPublicId("BILL-12345")).thenReturn(Optional.of(sampleBill));

        BillResponseDto response = billService.getBillByPublicId("BILL-12345");

        assertEquals(875.0, response.getTotalAmount());
    }

    @Test
    @DisplayName("deleteBillByPublicId: Should delete bill when found")
    void testDeleteBillByPublicId_Success() {
        when(billRepository.findByPublicId("BILL-12345")).thenReturn(Optional.of(sampleBill));

        billService.deleteBillByPublicId("BILL-12345");

        verify(billRepository).delete(sampleBill);
    }
}