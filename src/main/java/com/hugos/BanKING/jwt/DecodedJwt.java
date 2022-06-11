package com.hugos.BanKING.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Date;

@Getter
@AllArgsConstructor
public class DecodedJwt {
    private String subject;
    private String role;
    private String issuer;
    private Date issuedAt;
}
