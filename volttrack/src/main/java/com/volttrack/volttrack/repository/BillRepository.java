package com.volttrack.volttrack.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.volttrack.volttrack.entity.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByConsumer_Id(Long consumerId);

    List<Bill> findByMeter_Id(Long meterId);
}