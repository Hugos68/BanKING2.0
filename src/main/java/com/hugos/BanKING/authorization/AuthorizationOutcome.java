package com.hugos.BanKING.authorization;

import com.hugos.BanKING.role.Role;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class AuthorizationOutcome {
    private boolean isAuthorized;
    private String subject;
    private Role role;
    private HttpStatus status;
    private String message;
}
