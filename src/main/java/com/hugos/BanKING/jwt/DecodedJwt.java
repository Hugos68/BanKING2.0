package com.hugos.BanKING.jwt;

import lombok.Data;

import java.util.Date;

@Data
public class DecodedJwt {
    private String subject;
    private String role;
    private String issuer;
    private Date issuedAt;
}
