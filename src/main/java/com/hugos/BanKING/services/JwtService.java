package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.entities.AppUser;
import com.hugos.BanKING.helpobjects.DecodedAccessToken;
import com.hugos.BanKING.helpobjects.DecodedRefreshToken;
import com.hugos.BanKING.enums.Role;
import com.hugos.BanKING.repositories.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class JwtService {
    // Generate key with secret
    private final String API_SECRET = "kjlfds4124ho4h1l24hl1l1gkj41h4k1u4h12l";
    private final byte[] encoded = API_SECRET.getBytes(StandardCharsets.UTF_8);
    private final SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256");
    private final AppUserRepository appUserRepository;

    public Map<String, String> createAccessRefreshTokenPair(AppUser appUser) {

            // Prep data for payload
            String subject = appUser.getEmail();
            String role = appUser.getRole().name();
            String issuer = "BanKING2.0";
            Date now = new Date(System.currentTimeMillis());

            // Create access token
            String accessToken = Jwts.builder()
                    .setSubject(subject)
                    .claim("role", role)
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .signWith(secretKey)
                    .compact();

            // Create refresh token
            String refreshToken = Jwts.builder()
                    .setSubject(subject)
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
    public DecodedAccessToken decodeAccessToken(String token) {

        // Get payload from token
        Claims claims = getAllClaimsFromToken(token);
        if (claims==null) {
            return null;
        }

        // Get claims
        String subject = claims.get("sub", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        // Check if token is expired
        boolean isExpired = issuedAt.getTime() < System.currentTimeMillis() - 15 * 60 * 1000;

        // Return access token object
        return new DecodedAccessToken(subject, role, issuer, issuedAt, isExpired);
    }

    // Returns null if token is invalid
    public DecodedRefreshToken decodeRefreshToken(String token) {

        // Get payload from token
        Claims claims = getAllClaimsFromToken(token);
        if (claims==null) {
            return null;
        }

        // Get claims
        String subject = claims.get("sub", String.class);
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        // Check if token is expired
        boolean isExpired = issuedAt.getTime() < System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;


        // Return refresh token object
        return new DecodedRefreshToken(subject, issuer, issuedAt, isExpired);
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {

        // Retrieve and decode access token
        String refreshToken;
        try {
            refreshToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            refreshToken = null;
        }

        // Get decoded token from request
        DecodedRefreshToken decodedRefreshToken = decodeRefreshToken(refreshToken);

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Token validation
        if (decodedRefreshToken==null ||
            decodedRefreshToken.isExpired() ||
            appUserRepository.findByEmail(decodedRefreshToken.subject()).isEmpty() ) {
            jsonObject.addProperty("message", "Refresh token is invalid");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Get token pair (Note: refresh is not used to force authentication after 1 week)
        Map<String,String> tokenPair = createAccessRefreshTokenPair(
            appUserRepository.findByEmail(decodedRefreshToken.subject()).get()
        );

        // Create json response body
        jsonObject.addProperty("access_token", tokenPair.get("access_token"));
        jsonObject.addProperty("message", "Refresh token was validated");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
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

