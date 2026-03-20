package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
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
class AlertRepositoryTest {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Meter savedMeter;
    private Alert savedAlert;

    @BeforeEach
    void setUp() {
        // 1. Create User
        savedUser = User.builder()
                .username("alert_user_" + UUID.randomUUID().toString().substring(0, 4))
                .email("alerts@volttrack.com")
                .password("password")
                .role(Role.CONSUMER)
                .active(true)
                .publicId(UUID.randomUUID().toString())
                .build();
        userRepository.save(savedUser);

        savedMeter = Meter.builder()
                .location("North Wing")
                .user(savedUser)
                .status(Status.ONLINE)
                .build();
        meterRepository.save(savedMeter);

        Alert alert = Alert.builder()
                .publicId("ALT-VOLT-001")
                .meter(savedMeter)
                .alertType("OVERVOLTAGE")
                .message("High voltage detected: 250V")
                .createdAt(LocalDateTime.now())
                .build();

        savedAlert = alertRepository.save(alert);
    }

    @Test
    @DisplayName("Should find alerts by User ID (via Meter)")
    void shouldFindAlertsByConsumerId() {
        Page<Alert> alertPage = alertRepository.findByMeter_User_Id(savedUser.getId(), PageRequest.of(0, 10));

        assertThat(alertPage.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(alertPage.getContent().get(0).getMessage()).contains("High voltage");
    }

    @Test
    @DisplayName("Should find alerts by Meter ID")
    void shouldFindByMeterId() {
        List<Alert> alerts = alertRepository.findByMeter_Id(savedMeter.getId());

        assertThat(alerts).isNotEmpty();
        assertThat(alerts.get(0).getAlertType()).isEqualTo("OVERVOLTAGE");
    }

    @Test
    @DisplayName("Should find alert by Public ID")
    void shouldFindByPublicId() {
        Optional<Alert> found = alertRepository.findByPublicId("ALT-VOLT-001");

        assertThat(found).isPresent();
        assertThat(found.get().getPublicId()).isEqualTo("ALT-VOLT-001");
    }

    @Test
    @DisplayName("Should find alerts by Alert Type")
    void shouldFindByAlertType() {
        List<Alert> alerts = alertRepository.findByAlertType("OVERVOLTAGE");

        assertThat(alerts).hasSizeGreaterThanOrEqualTo(1);
        assertThat(alerts.get(0).getAlertType()).isEqualTo("OVERVOLTAGE");
    }

    @Test
    @DisplayName("Should return empty list for invalid Alert Type")
    void shouldReturnEmptyForUnknownType() {
        List<Alert> alerts = alertRepository.findByAlertType("LOW_BATTERY");
        assertThat(alerts).isEmpty();
    }
}