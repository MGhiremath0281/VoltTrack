package com.volttrack.volttrack.dto.meter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReadingDTO {
    private Long id;
    private String publicId;

    @NotNull(message = "Meter ID is required")
    private Long meterId;

    @Positive(message = "Pulse count must be positive")
    private Integer pulseCount;

    @Positive(message = "Voltage must be positive")
    private Double voltage;

    @Positive(message = "Current must be positive")
    private Double current;

    @Positive(message = "Units consumed must be positive")
    private Double unitsConsumed;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}
