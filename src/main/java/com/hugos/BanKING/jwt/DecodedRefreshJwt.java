package com.hugos.BanKING.jwt;

import java.util.Date;

public class DecodedRefreshJwt extends DecodedJwt {

    public DecodedRefreshJwt(String subject, String issuer, Date issuedAt, boolean isExpired) {
        super(subject, issuer, issuedAt, isExpired);
    }
}
