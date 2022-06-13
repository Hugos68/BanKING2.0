package com.hugos.BanKING.authorization;

import com.hugos.BanKING.role.Role;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class AuthorizationResponse {
    private boolean isAuthorized;
    private Role role;
    private HttpStatus status;
    private String message;
}
