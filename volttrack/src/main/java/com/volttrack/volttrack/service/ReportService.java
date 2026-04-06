package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.report.ConsumptionReportDto;
import com.volttrack.volttrack.dto.report.RevenueReportDto;
import com.volttrack.volttrack.dto.report.OfficerPerformanceDto;
import com.volttrack.volttrack.dto.report.CustomerSegmentationDto;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ConsumptionReportDto getConsumptionReport(Long subDistrictOfficerId, String range);
    RevenueReportDto getRevenueReport(Long subDistrictOfficerId, String range);
    OfficerPerformanceDto getOfficerPerformance(Long subDistrictOfficerId, Pageable pageable);
    CustomerSegmentationDto getCustomerSegmentation(Long subDistrictOfficerId);
}
