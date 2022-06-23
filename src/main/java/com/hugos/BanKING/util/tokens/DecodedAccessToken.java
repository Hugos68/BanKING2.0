package com.hugos.BanKING.util.tokens;

import com.hugos.BanKING.enums.Role;
import java.util.Date;

public record DecodedAccessToken(String subject, Role role, String issuer, Date issuedAt, boolean isExpired) {}