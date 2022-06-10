package com.hugos.BanKING.authenticate;

import lombok.Data;

@Data
public class AuthenticateRequest {
    private String email;
    private String password;
}
