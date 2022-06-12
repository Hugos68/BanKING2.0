package com.hugos.BanKING.jsonwebtoken.tokens;

import java.util.Date;

public record DecodedRefreshToken(String subject, String issuer, Date issuedAt, boolean isExpired) {}
