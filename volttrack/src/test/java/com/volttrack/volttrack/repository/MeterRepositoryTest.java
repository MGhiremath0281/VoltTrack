package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterRepositoryTest {

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Meter savedMeter;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .username("meter_owner_" + UUID.randomUUID().toString().substring(0, 4))
                .email("owner@volttrack.com")
                .password("pass123")
                .role(Role.CONSUMER)
                .active(true)
                .publicId(UUID.randomUUID().toString())
                .build();
        userRepository.save(owner);

        Meter meter = Meter.builder()
                .location("Main Block - A")
                .user(owner) // Link to saved user
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();

        savedMeter = meterRepository.save(meter);
    }

    @Test
    @DisplayName("Should find meter by User ID")
    void shouldFindByUserId() {
        Optional<Meter> found = meterRepository.findByUser_Id(owner.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLocation()).isEqualTo("Main Block - A");
        assertThat(found.get().getUser().getUsername()).isEqualTo(owner.getUsername());
    }

    @Test
    @DisplayName("Should verify custom ID generation logic")
    void shouldGenerateCustomIds() {
        assertThat(savedMeter.getPublicId()).startsWith("MTR-");
        assertThat(savedMeter.getMeterId()).startsWith("ELEC-");
    }

    @Test
    @DisplayName("Should check if meter ID exists")
    void shouldCheckExistsByMeterId() {
        boolean exists = meterRepository.existsByMeterId(savedMeter.getMeterId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find meter by Public ID")
    void shouldFindByPublicId() {
        Optional<Meter> found = meterRepository.findByPublicId(savedMeter.getPublicId());

        assertThat(found).isPresent();
        assertThat(found.get().getMeterId()).isEqualTo(savedMeter.getMeterId());
    }

    @Test
    @DisplayName("Should handle paginated meters for a specific user")
    void shouldFindPaginatedMetersByUserId() {
        Meter secondMeter = Meter.builder()
                .location("Backyard - Section B")
                .user(owner)
                .status(Status.ONLINE)
                .billing(BillingCycle.MONTHLY)
                .build();
        meterRepository.save(secondMeter);

        Page<Meter> page = meterRepository.findByUser_Id(owner.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Meter::getLocation)
                .contains("Main Block - A", "Backyard - Section B");
    }
}