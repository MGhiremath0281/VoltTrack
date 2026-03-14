package com.volttrack.volttrack.dto.alert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequestDto {
    @NotNull(message = "Meter ID is required")
    private Long meterId;

    @NotBlank(message = "Alert type is required")
    private String alertType;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "CreatedAt timestamp is required")
    private LocalDateTime createdAt;
}
