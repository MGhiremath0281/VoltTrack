package com.volttrack.volttrack.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String username;
    private String email;
    private String password;
    private String role; 
    private Boolean active; 
}
