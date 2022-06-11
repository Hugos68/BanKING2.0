package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class jwtService {

    @SneakyThrows
    public String encode(AppUser appUser) {

        // TODO convert appUser into jwt

        // Prep data for payload
        String subject = appUser.getEmail();
        String role = appUser.getRole().name();
        String issuer = "BanKING2.0";
        Date now = new Date(System.currentTimeMillis());

        return null;
    }

    public DecodedJwt decode(String jwt) {

        // TODO convert encoded jwt into DecodedJwt object

        String subject = null;
        String role = null;
        String issuer = null;
        Date issuedAt = null;

        return new DecodedJwt(
            subject,
            role,
            issuer,
            issuedAt
        );
    }
}
