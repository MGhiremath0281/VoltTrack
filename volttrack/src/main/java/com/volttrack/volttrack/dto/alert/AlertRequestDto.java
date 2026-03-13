package com.volttrack.volttrack.dto.alert;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequestDto {
    private Long meterId;
    private String alertType;
    private String message;
    private LocalDateTime createdAt;
}
