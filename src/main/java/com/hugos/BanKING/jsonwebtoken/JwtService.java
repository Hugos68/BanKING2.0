package com.hugos.BanKING.jsonwebtoken;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.appuser.AppUserService;
import com.hugos.BanKING.jsonwebtoken.tokens.DecodedAccessToken;
import com.hugos.BanKING.jsonwebtoken.tokens.DecodedRefreshToken;
import com.hugos.BanKING.role.Role;
import com.hugos.BanKING.util.RequestService;
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

@Service
@AllArgsConstructor
public class JwtService {

    private final RequestService requestService;
    private final AppUserService appUserService;

    public ResponseEntity<?> refresh(HttpServletRequest request) {

        // Prep response entity
        Map<String, String> responseMap = new HashMap<>();
        HttpStatus status = null;
        String message = null;

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);

        // Get decoded token from request
        DecodedRefreshToken decodedRefreshToken = decodeRefreshToken(body.get("refresh_token").getAsString());

        // Validate token
        if (decodedRefreshToken==null) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Invalid token";
        }
        else {
            status = HttpStatus.OK;
            message = "Valid token";
        }

        // Check if something was wrong, if so, return 400 code
        if (status!= HttpStatus.OK) {
            responseMap.put("message", message);
            String responseBody = new Gson().toJson(responseMap);
            return ResponseEntity.status(status).body(responseBody);
        }

        Map<String,String> tokenPair = createAccessRefreshTokenPair(
            appUserService.findByEmail(decodedRefreshToken.subject()).get()
        );

        // Create json body
        responseMap.put("message", message);
        responseMap.put("access_token", tokenPair.get("access_token"));
        responseMap.put("refresh_token", tokenPair.get("refresh_token"));
        String responseBody = new Gson().toJson(responseMap);

        // Respond to request
        return ResponseEntity.status(status).body(responseBody);
    }

    // Generate key with secret
    private final String API_SECRET = "kjlfds4124ho4h1l24hl1l1gkj41h4k1u4h12l";
    byte[] encoded = API_SECRET.getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = new SecretKeySpec(encoded, "HmacSHA256");

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

        // Get shared claims
        String subject = claims.get("sub", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        // Check if expired (expire interval is 15 minutes)
        boolean isExpired = issuedAt.getTime() > System.currentTimeMillis() - 15 * 60 * 1000;

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

        // Get shared claims
        String subject = claims.get("sub", String.class);
        String issuer = claims.get("iss", String.class);
        Date issuedAt = claims.get("iat", Date.class);

        // Check if expired (expire interval is 30 days)
        boolean isExpired = issuedAt.getTime() > System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;

        // Return access token object
        return new DecodedRefreshToken(subject, issuer, issuedAt, isExpired);
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
]
}

