package com.volttrack.volttrack.dto.meter;

import com.volttrack.volttrack.entity.enums.BillingCycle;
import com.volttrack.volttrack.entity.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterResponseDto {

    private Long id;
    private String publicId;
    private String meterId;
    private String location;
    private String userPublicId;
    private Status status;
    private BillingCycle billing;
}