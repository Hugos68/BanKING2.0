package com.hugos.BanKING.jsonwebtoken;

import com.hugos.BanKING.role.Role;
import lombok.AllArgsConstructor;
import java.util.Date;

@AllArgsConstructor
public class DecodedAccessToken extends DecodedJwt {

    private final String subject;
    private final Role role;
    private final String issuer;
    private final Date issuedAt;
    private final boolean isExpired;
}
