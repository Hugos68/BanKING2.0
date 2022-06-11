package com.hugos.BanKING.jwt;

import lombok.AllArgsConstructor;
import java.util.Date;

@AllArgsConstructor
public class DecodedJwt {
    private final String subject;
    private final String issuer;
    private final Date issuedAt;
    private final boolean isExpired;
}
