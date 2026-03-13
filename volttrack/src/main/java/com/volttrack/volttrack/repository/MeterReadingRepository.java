package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    // Get the latest reading for a meter (closing reading)
    Optional<MeterReading> findTopByMeter_IdOrderByTimestampDesc(Long meterId);

    // Get the earliest reading for a meter (opening reading)
    Optional<MeterReading> findTopByMeter_IdOrderByTimestampAsc(Long meterId);
}
