package com.hugos.BanKING.models;

import com.hugos.BanKING.enums.Role;
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
