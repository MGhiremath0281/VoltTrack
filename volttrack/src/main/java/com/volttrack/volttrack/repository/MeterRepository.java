package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {

    Optional<Meter> findByUser_Id(Long userId);
    List<Meter> findByUser(User user);

    Page<Meter> findByUser_Id(Long userId, Pageable pageable);
    boolean existsByMeterId(String meterId);
    Optional<Meter> findByPublicId(String publicId);
}
