package com.event.Flowvent.auth;

import com.event.Flowvent.user.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}