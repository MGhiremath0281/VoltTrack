package com.volttrack.volttrack.repository;

import com.volttrack.volttrack.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByMeter_Id(Long meterId);

    List<Alert> findByAlertType(String alertType);
}

