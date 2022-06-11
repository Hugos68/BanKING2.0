package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {

    // Generate key with secret
    private final String API_SECRET = "kjlfds4124ho4h1l24hl1l1gkj41h4k1u4h12l";
    byte[] encoded = API_SECRET.getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256");


    @SneakyThrows
    public Map<String, String> createAccessRefreshTokenPair(AppUser appUser) {

            // Prep data for payload
            String subject = appUser.getEmail();
            String role = appUser.getRole().name();
            String issuer = "BanKING2.0";
            Date now = new Date(System.currentTimeMillis());

            // Create access token
            String accessToken = Jwts.builder()
                    .setSubject(subject)
                    .claim("type", JwtType.ACCESS)
                    .claim("role", role)
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .signWith(secretKey)
                    .compact();

            // TODO: Create refresh token
            // Create refresh token
            String refreshToken = Jwts.builder()
                    .setSubject(subject)
                    .claim("type", JwtType.REFRESH)
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .signWith(secretKey)
                    .compact();

            // Return token pair
            Map<String, String> tokenPair = new HashMap<>();
            tokenPair.put("access_token", accessToken);
            tokenPair.put("refresh_token", refreshToken);
            return tokenPair;
    }

    // Returns null when token is invalid
    public DecodedJwt decodeToken(String jwt) {
        Claims claims = getAllClaimsFromToken(jwt);
        if (claims==null) {
            return null;
        }

        // Get shared claims
        String subject = claims.get("sub", String.class);
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        // Handle access token
        if (claims.get("type").equals(JwtType.ACCESS)) {

            Role role = Role.valueOf(claims.get("role", String.class));

            // Check if access token is older than 15 minutes
            boolean isExpired = issuedAt.getTime() > System.currentTimeMillis() - 15 * 60 * 1000;

            return new DecodedAccessJwt(subject, role, issuer, issuedAt, isExpired);
        }

        // Handle refresh token
        else {
            // Check if refresh token is older than 30 days
            boolean isExpired = issuedAt.getTime() > System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;

            return new DecodedRefreshJwt(subject, issuer, issuedAt, isExpired);
        }
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

