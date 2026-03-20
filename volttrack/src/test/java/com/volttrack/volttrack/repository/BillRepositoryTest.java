package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Bill;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.BillStatus;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.entity.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BillRepositoryTest {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedConsumer;
    private Meter savedMeter;
    private Bill savedBill;

    @BeforeEach
    void setUp() {
        savedConsumer = User.builder()
                .username("bill_user_" + UUID.randomUUID().toString().substring(0, 4))
                .email("bill@volttrack.com")
                .password("pass")
                .role(Role.CONSUMER)
                .active(true)
                .publicId(UUID.randomUUID().toString())
                .build();
        userRepository.save(savedConsumer);

        savedMeter = Meter.builder()
                .location("Sector 7G")
                .user(savedConsumer)
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();
        meterRepository.save(savedMeter);

        Bill bill = Bill.builder()
                .publicId("BILL-TEST-001")
                .meter(savedMeter)
                .consumer(savedConsumer)
                .billingCycle(BillingCycle.MONTHLY)
                .cycleStartDate(LocalDateTime.now().minusMonths(1))
                .cycleEndDate(LocalDateTime.now())
                .openingReading(1200.0)
                .closingReading(1500.0)
                .unitsConsumed(300.0)
                .totalAmount(450.75)
                .status(BillStatus.UNPAID)
                .generatedAt(LocalDateTime.now())
                .build();

        savedBill = billRepository.save(bill);
    }

    @Test
    @DisplayName("Should find bills by Consumer ID with pagination")
    void shouldFindByConsumerId() {
        Page<Bill> billPage = billRepository.findByConsumer_Id(savedConsumer.getId(), PageRequest.of(0, 10));

        assertThat(billPage.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(billPage.getContent().get(0).getConsumer().getId()).isEqualTo(savedConsumer.getId());
    }

    @Test
    @DisplayName("Should find all bills for a specific meter")
    void shouldFindByMeterId() {
        List<Bill> bills = billRepository.findByMeter_Id(savedMeter.getId());

        assertThat(bills).hasSize(1);
        assertThat(bills.get(0).getMeter().getId()).isEqualTo(savedMeter.getId());
        assertThat(bills.get(0).getTotalAmount()).isEqualTo(450.75);
    }

    @Test
    @DisplayName("Should find bill by Public ID")
    void shouldFindByPublicId() {
        Optional<Bill> found = billRepository.findByPublicId("BILL-TEST-001");

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(BillStatus.UNPAID);
    }

    @Test
    @DisplayName("Should return empty list for meter with no bills")
    void shouldReturnEmptyForNoBills() {
        Meter newMeter = Meter.builder()
                .location("Sector 9")
                .user(savedConsumer)
                .build();
        meterRepository.save(newMeter);

        List<Bill> bills = billRepository.findByMeter_Id(newMeter.getId());
        assertThat(bills).isEmpty();
    }
}