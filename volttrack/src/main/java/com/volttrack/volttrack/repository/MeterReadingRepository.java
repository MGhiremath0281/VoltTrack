package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    
}
