package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volttrack.volttrack.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    // ✅ Add this to check if email already exists
    boolean existsByEmail(String email);

    // ✅ Add this to fetch consumers
    P
    age<User> findByRole(Role role, Pageable pageable);

}