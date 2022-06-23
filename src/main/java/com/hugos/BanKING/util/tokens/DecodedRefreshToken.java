package com.hugos.BanKING.util.tokens;

import java.util.Date;

public record DecodedRefreshToken(String subject, String issuer, Date issuedAt, boolean isExpired) {}