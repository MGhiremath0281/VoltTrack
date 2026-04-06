package com.volttrack.volttrack.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueReportDto {
    private String range;              // e.g., "monthly"
    private List<String> labels;       // e.g., ["Jan", "Feb", "Mar"]
    private List<Double> collected;    // amounts collected per label
    private List<Double> outstanding;  // unpaid amounts per label
    private Double totalCollected;     // aggregated collected
    private Double totalOutstanding;   // aggregated outstanding
}
