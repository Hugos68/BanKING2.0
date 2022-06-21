package com.hugos.BanKING.util;

import java.util.Date;

public record DecodedRefreshToken(String subject, String issuer, Date issuedAt, boolean isExpired) {}