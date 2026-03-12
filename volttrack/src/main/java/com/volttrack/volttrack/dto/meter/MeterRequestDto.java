package com.volttrack.volttrack.dto.meter;

import com.volttrack.volttrack.entity.Status;
import com.volttrack.volttrack.entity.Billing;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterRequestDto {
    private String meterId;
    private String location;
    private Long userId;
    private Status status;
    private Billing billing;
}
