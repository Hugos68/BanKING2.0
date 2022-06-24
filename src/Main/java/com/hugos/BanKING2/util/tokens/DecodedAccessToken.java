package com.hugos.BanKING2.util.tokens;

import com.hugos.BanKING2.enums.Role;
import java.util.Date;

public record DecodedAccessToken(String subject, Role role, String issuer, Date issuedAt, boolean isExpired) {}