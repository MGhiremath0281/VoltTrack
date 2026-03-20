package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.MeterReading;
import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterReadingRepositoryTest {

    @Autowired
    private MeterReadingRepository readingRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private UserRepository userRepository;

    private Meter savedMeter;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("reader_user_" + UUID.randomUUID().toString().substring(0, 4))
                .email("reader@volttrack.com")
                .password("pass")
                .role(Role.CONSUMER)
                .active(true)
                .publicId(UUID.randomUUID().toString())
                .build();
        userRepository.save(user);

        Meter meter = Meter.builder()
                .location("Lab 101")
                .user(user)
                .build();
        savedMeter = meterRepository.save(meter);

        MeterReading oldReading = MeterReading.builder()
                .publicId("READ-OLD")
                .meter(savedMeter)
                .pulseCount(100)
                .voltage(230.0)
                .current(5.0)
                .unitsConsumed(10.5)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build();

        MeterReading newReading = MeterReading.builder()
                .publicId("READ-NEW")
                .meter(savedMeter)
                .pulseCount(200)
                .voltage(228.5)
                .current(5.2)
                .unitsConsumed(25.0)
                .timestamp(LocalDateTime.now())
                .build();

        readingRepository.save(oldReading);
        readingRepository.save(newReading);
    }

    @Test
    @DisplayName("Should find the LATEST reading for a meter")
    void shouldFindLatestReading() {
        Optional<MeterReading> latest = readingRepository.findTopByMeter_IdOrderByTimestampDesc(savedMeter.getId());

        assertThat(latest).isPresent();
        assertThat(latest.get().getPublicId()).isEqualTo("READ-NEW");
        assertThat(latest.get().getPulseCount()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should find the EARLIEST reading for a meter")
    void shouldFindEarliestReading() {
        Optional<MeterReading> earliest = readingRepository.findTopByMeter_IdOrderByTimestampAsc(savedMeter.getId());

        assertThat(earliest).isPresent();
        assertThat(earliest.get().getPublicId()).isEqualTo("READ-OLD");
        assertThat(earliest.get().getPulseCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should find reading by Public ID")
    void shouldFindByPublicId() {
        Optional<MeterReading> found = readingRepository.findByPublicId("READ-NEW");

        assertThat(found).isPresent();
        assertThat(found.get().getVoltage()).isEqualTo(228.5);
    }

    @Test
    @DisplayName("Should return empty when meter has no readings")
    void shouldReturnEmptyForMeterWithNoData() {

        Meter emptyMeter = Meter.builder()
                .location("Empty Room")
                .user(savedMeter.getUser())
                .build();
        meterRepository.save(emptyMeter);

        Optional<MeterReading> reading = readingRepository.findTopByMeter_IdOrderByTimestampDesc(emptyMeter.getId());
        assertThat(reading).isEmpty();
    }
}