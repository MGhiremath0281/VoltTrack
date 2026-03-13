package com.volttrack.volttrack.dto.meter;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReadingDTO {
    private Long id;
    private String meterId;
    private Integer pulseCount;
    private Double voltage;
    private Double current;
    private Double unitsConsumed;
    private LocalDateTime timestamp;
}
