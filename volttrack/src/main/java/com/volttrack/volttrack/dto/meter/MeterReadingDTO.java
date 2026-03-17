package com.volttrack.volttrack.dto.meter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReadingDTO {

    private String publicId;   // ✅ exposed ID

    @NotBlank(message = "Meter Public ID is required")
    private String meterPublicId;

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