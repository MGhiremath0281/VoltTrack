package com.volttrack.volttrack.dto.bill;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponseDto {

    private String publicId;

    private String meterPublicId;
    private String consumerPublicId;

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