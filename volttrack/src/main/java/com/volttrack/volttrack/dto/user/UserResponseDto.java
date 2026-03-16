package com.volttrack.volttrack.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String publicId;  // external role-prefixed id
    private String username;
    private String email;
    private String role;
    private Boolean active;
}
