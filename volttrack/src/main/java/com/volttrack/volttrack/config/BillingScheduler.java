package com.volttrack.volttrack.config;

import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.service.BillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused") // Suppress IDE warning about 'never used'
@Component
@RequiredArgsConstructor
@Slf4j
public class BillingScheduler {

    private final MeterRepository meterRepository;
    private final BillService billService;

    /**
     * Runs every 5 minutes for testing purposes.
     * Change cron to "0 0 0 1 * ?" for real monthly billing (1st of month at midnight).
     */

    @Scheduled(cron = "0 */5 * * * ?")
    public void generateMonthlyBills() {

        log.info("🔥 Scheduler triggered at {}", LocalDateTime.now());
        log.info("Starting monthly bill generation...");

        List<Meter> meters = meterRepository.findAll();
        log.info("Found {} meters to generate bills for.", meters.size());

        for (Meter meter : meters) {
            try {
                billService.generateBillForConsumer(meter.getUser().getPublicId());
                log.info("✅ Bill generated for meter: {}", meter.getPublicId());
            } catch (Exception e) {
                log.error("❌ Failed to generate bill for meter {}: {}", meter.getPublicId(), e.getMessage(), e);
            }
        }

        log.info("Monthly bill generation completed at {}", LocalDateTime.now());
    }
}