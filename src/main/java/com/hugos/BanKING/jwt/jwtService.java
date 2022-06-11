package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.role.Role;
import com.zaxxer.hikari.util.ClockSource;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class jwtService {

    // Generate key with secret
    private final String API_SECRET = "kjlfds4124ho4h1l24hl1l1gkj41h4k1u4h12l";
    byte[] encoded = API_SECRET.getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256");


    @SneakyThrows
    public String encode(AppUser appUser) {

        // Prep data for payload
        String subject = appUser.getEmail();
        String role = appUser.getRole().name();
        String issuer = "BanKING2.0";
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .setSubject(subject)
                .claim("email", appUser.getEmail())
                .claim("role",role)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .signWith(secretKey)
                .compact();
    }

    // Returns null when decoding unsuccessful
    public DecodedJwt decode(String jwt) {

        Claims claims = getAllClaimsFromToken(jwt);

        if (claims==null) {
            return null;
        }

        String subject = claims.get("sub", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        return new DecodedJwt(
            subject,
            role,
            issuer,
            issuedAt
        );
    }

    private Claims getAllClaimsFromToken(String jwt) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
}
