package com.hugos.BanKING2.util.tokens;

import java.util.Date;

public record DecodedRefreshToken(String subject, String issuer, Date issuedAt, boolean isExpired) {}