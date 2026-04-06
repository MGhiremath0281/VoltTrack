package com.volttrack.volttrack.service.impl;

import com.volttrack.volttrack.dto.report.ConsumptionReportDto;
import com.volttrack.volttrack.dto.report.RevenueReportDto;
import com.volttrack.volttrack.dto.report.OfficerPerformanceDto;
import com.volttrack.volttrack.dto.report.CustomerSegmentationDto;
import com.volttrack.volttrack.repository.MeterRepository;
import com.volttrack.volttrack.repository.BillRepository;
import com.volttrack.volttrack.repository.UserRepository;
import com.volttrack.volttrack.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MeterRepository meterRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;

    @Override
    public ConsumptionReportDto getConsumptionReport(Long subDistrictOfficerId, String range) {
        List<String> labels = List.of("Jan", "Feb", "Mar");
        List<Double> consumption = List.of(1200.0, 1350.5, 1100.0);
        Double total = consumption.stream().mapToDouble(Double::doubleValue).sum();

        return ConsumptionReportDto.builder()
                .range(range)
                .labels(labels)
                .consumption(consumption)
                .totalConsumption(total)
                .build();
    }

    @Override
    public RevenueReportDto getRevenueReport(Long subDistrictOfficerId, String range) {
        List<String> labels = List.of("Jan", "Feb", "Mar");
        List<Double> collected = List.of(50000.0, 52000.0, 48000.0);
        List<Double> outstanding = List.of(5000.0, 4500.0, 6000.0);

        return RevenueReportDto.builder()
                .range(range)
                .labels(labels)
                .collected(collected)
                .outstanding(outstanding)
                .totalCollected(collected.stream().mapToDouble(Double::doubleValue).sum())
                .totalOutstanding(outstanding.stream().mapToDouble(Double::doubleValue).sum())
                .build();
    }

    @Override
    public OfficerPerformanceDto getOfficerPerformance(Long subDistrictOfficerId, Pageable pageable) {
        List<String> officerNames = List.of("Officer A", "Officer B");
        List<Integer> metersHandled = List.of(120, 95);
        List<Integer> approvalsDone = List.of(80, 70);
        List<Integer> complaintsResolved = List.of(15, 20);

        return OfficerPerformanceDto.builder()
                .officerNames(officerNames)
                .metersHandled(metersHandled)
                .approvalsDone(approvalsDone)
                .complaintsResolved(complaintsResolved)
                .build();
    }

    @Override
    public CustomerSegmentationDto getCustomerSegmentation(Long subDistrictOfficerId) {
        Map<String, Long> segments = Map.of(
                "Residential", 120L,
                "Commercial", 45L,
                "Industrial", 10L
        );

        return CustomerSegmentationDto.builder()
                .segments(segments)
                .build();
    }
}
