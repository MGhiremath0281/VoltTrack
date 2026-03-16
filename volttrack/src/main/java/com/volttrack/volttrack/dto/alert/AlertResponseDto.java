package com.volttrack.volttrack.dto.alert;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponseDto {
    private Long id;
    private String publicId;
    private Long meterId;
    private String alertType;
    private String message;
    private LocalDateTime createdAt;
}
