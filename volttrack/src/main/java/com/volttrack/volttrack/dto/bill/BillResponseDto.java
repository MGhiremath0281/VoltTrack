package com.volttrack.volttrack.dto.bill;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponseDto {
    private Long id;
    private String publicId;
    private Long meterId;
    private Long consumerId;
    private String billingCycle;
    private LocalDateTime cycleStartDate;
    private LocalDateTime cycleEndDate;
    private Double openingReading;
    private Double closingReading;
    private Double unitsConsumed;
    private Double baseAmount;
    private Double fixedCharges;
    private Double taxAmount;
    private Double totalAmount;
    private String status;
    private LocalDateTime generatedAt;
    private LocalDateTime dueDate;
}

