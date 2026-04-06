package com.volttrack.volttrack.dto.user;

import com.volttrack.volttrack.dto.meter.MeterResponseDto;
import com.volttrack.volttrack.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Boolean active;
    private String publicId;
    private MeterResponseDto meter;

    // New fields for officer management
    private Long approvedBy;   // ID of sub-district officer who approved
    private Boolean rejected;  // flag if registration was rejected
    private Long suspendedBy;  // ID of sub-district officer who suspended
}
