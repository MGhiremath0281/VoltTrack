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
public class ConsumptionReportDto {
    private String range;
    private List<String> labels;
    private List<Double> consumption;
    private Double totalConsumption;
}
