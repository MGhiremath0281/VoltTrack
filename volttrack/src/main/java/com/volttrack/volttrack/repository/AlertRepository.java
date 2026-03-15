package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Alert;
import com.volttrack.volttrack.entity.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Meter> findByUser_Id(Long userId);
    Page<Alert> findByMeter_User_Id(Long consumerId, Pageable pageable);


    List<Alert> findByMeter_Id(Long meterId);

    List<Alert> findByAlertType(String alertType);
}

