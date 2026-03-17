package com.volttrack.volttrack.dto.meter;

import com.volttrack.volttrack.entity.enums.Status;
import com.volttrack.volttrack.entity.enums.Billing;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterRequestDto {

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "User Public ID is required")
    private String userPublicId;

    @NotNull(message = "Status is required")
    private Status status;

    private Billing billing;
}