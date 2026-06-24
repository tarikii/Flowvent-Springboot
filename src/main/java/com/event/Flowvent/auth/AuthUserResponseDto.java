package com.event.Flowvent.auth;

import com.event.Flowvent.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponseDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
}