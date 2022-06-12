package com.hugos.BanKING.jsonwebtoken.tokens;

import com.hugos.BanKING.role.Role;
import java.util.Date;

public record DecodedAccessToken(String subject, Role role, String issuer, Date issuedAt, boolean isExpired) {}
