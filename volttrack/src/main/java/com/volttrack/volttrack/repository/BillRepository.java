package com.volttrack.volttrack.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.volttrack.volttrack.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Page<Bill> findByConsumer_Id(Long consumerId, Pageable pageable);

    List<Bill> findByMeter_Id(Long meterId);
}