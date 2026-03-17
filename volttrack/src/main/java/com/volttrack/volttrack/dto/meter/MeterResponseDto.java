package com.volttrack.volttrack.dto.meter;

import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.entity.enums.Billing;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterResponseDto {
    private Long id;
    private String userPublicId;
    private String meterId;
    private String location;
    private Long userId;
    private Status status;
    private Billing billing;
}
