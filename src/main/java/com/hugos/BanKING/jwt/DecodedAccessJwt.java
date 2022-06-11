package com.hugos.BanKING.jwt;

import com.hugos.BanKING.role.Role;
import java.util.Date;

public class DecodedAccessJwt extends DecodedJwt {

    private final Role role;

    public DecodedAccessJwt(String subject, Role role, String issuer, Date issuedAt, boolean isExpired) {
        super(subject, issuer, issuedAt, isExpired);
        this.role = role;
    }
}
