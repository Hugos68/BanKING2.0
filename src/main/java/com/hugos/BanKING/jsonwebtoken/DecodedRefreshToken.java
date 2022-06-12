package com.hugos.BanKING.jsonwebtoken;

import lombok.AllArgsConstructor;
import java.util.Date;

@AllArgsConstructor
public class DecodedRefreshToken extends DecodedJwt {
    private final String subject;
    private final String issuer;
    private final Date issuedAt;
    private final boolean isExpired;
}
