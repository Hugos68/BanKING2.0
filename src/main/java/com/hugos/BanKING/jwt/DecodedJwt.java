package com.hugos.BanKING.jwt;

import com.hugos.BanKING.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Date;

@Getter
@AllArgsConstructor
public class DecodedJwt {
    private String subject;
    private Role role;
    private String issuer;
    private Date issuedAt;
}
