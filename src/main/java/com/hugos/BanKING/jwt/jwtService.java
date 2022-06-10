package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class jwtService {

    // Creating signing algorithm
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // Secret key that belongs to api (altering invalidates all JWTs)
    private final String SECRET_KEY = "bdHWfs47DFHh5qG6fsju5";

    // Parse key into bytes to slow down BFAs
    private final byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);

    @SneakyThrows
    public String encode(AppUser appUser) {

        // Prep data for payload
        String id = UUID.randomUUID().toString();
        String subject = appUser.getEmail();
        String issuer = "BanKING2.0";
        Map<String, Object> claims = new HashMap<>();
        claims.put("ROLE", appUser.getRole().name());
        Date now = new Date(System.currentTimeMillis());

        // Assign data to jwt
        JwtBuilder builder = Jwts.builder()
            .setId(id)
            .setSubject(subject)
            .setClaims(claims)
            .setIssuer(issuer)
            .setIssuedAt(now)
            .signWith(signatureAlgorithm, secretKeyBytes);

        return builder.compact();
    }

    // Create jwt validator
    DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(signatureAlgorithm,
        new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName()
    ));

    public DecodedJwt decode(String jwt) {
        // TODO convert encoded jwt into DecodedJwt object
        return null;
    }
}
