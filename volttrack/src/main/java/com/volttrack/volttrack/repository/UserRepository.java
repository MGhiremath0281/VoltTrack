package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.enums.Role;
import com.volttrack.volttrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    Optional<User> findByPublicId(String publicId);

    Page<User> findByRoleAndUsernameContainingIgnoreCase(Role role, String username, Pageable pageable);

    long countByRole(Role role);

    Page<User> findByRoleAndActive(Role role, boolean active, Pageable pageable);

    Page<User> findByApprovedBy(Long approverId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 'CONSUMER' AND u.subDistrictOfficer.id = :subDistrictOfficerId")
    Page<User> findConsumersBySubDistrictOfficer(@Param("subDistrictOfficerId") Long subDistrictOfficerId, Pageable pageable);
}
