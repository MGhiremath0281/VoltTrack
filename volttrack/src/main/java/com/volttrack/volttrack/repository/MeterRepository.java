package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {

    Optional<Meter> findByUser_Id(Long userId);

    Page<Meter> findByUser_Id(Long userId, Pageable pageable);

    Optional<Meter> findByPublicId(String publicId);
}
