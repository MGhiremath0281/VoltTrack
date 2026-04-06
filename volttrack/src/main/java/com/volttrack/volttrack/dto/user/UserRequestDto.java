package com.volttrack.volttrack.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private String role;

    private Boolean active;

    // New fields for officer onboarding
    private Long approvedBy;   // ID of sub-district officer who approved
    private Boolean rejected;  // flag if registration was rejected
    private Long suspendedBy;  // ID of sub-district officer who suspended
}
