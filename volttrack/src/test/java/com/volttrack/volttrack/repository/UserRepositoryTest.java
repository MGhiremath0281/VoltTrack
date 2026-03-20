package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.User;
import com.volttrack.volttrack.entity.enums.Role;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // Create a user using the Builder pattern from your Entity
        User user = User.builder()
                .username("test_user_" + UUID.randomUUID().toString().substring(0, 5))
                .email("test_" + UUID.randomUUID().toString().substring(0, 5) + "@volttrack.com")
                .password("securePass123")
                .role(Role.ADMIN)
                .active(true)
                .build();

        // save() triggers @PrePersist which generates the publicId
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindByUsername() {
        Optional<User> found = userRepository.findByUsername(savedUser.getUsername());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    @DisplayName("Should verify publicId is generated automatically")
    void shouldHaveGeneratedPublicId() {
        assertThat(savedUser.getPublicId()).isNotNull();
        assertThat(savedUser.getPublicId()).contains("-"); // Check if it's a UUID string
    }

    @Test
    @DisplayName("Should return true for existing email")
    void shouldCheckExistsByEmail() {
        boolean exists = userRepository.existsByEmail(savedUser.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find user by Public ID")
    void shouldFindByPublicId() {
        Optional<User> found = userRepository.findByPublicId(savedUser.getPublicId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    @DisplayName("Should handle pagination for roles")
    void shouldHandlePagination() {
        // Save one more user to test "Page" results
        User user2 = User.builder()
                .username("second_user")
                .email("second@volttrack.com")
                .password("pass")
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(user2);

        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }
}