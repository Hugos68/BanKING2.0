package com.hugos.BanKING.util;
;
import com.hugos.BanKING.appuser.AppUser;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    // Creating signing algorithm
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // Secret key that belongs to api (altering invalidates all JWTs)
    private final String SECRET_KEY = "bdHWfs47DFHh5qG6fsju5";

    // Parse key into bytes to slow down BFAs
    private final byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);

    @SneakyThrows
    public String encode(AppUser appUser, Date exp) {

        // Prep data for payload
        String id = appUser.getId().toString();
        String subject = appUser.getEmail();
        String issuer = "BanKING2.0";
        Map<String, Object> claims = new HashMap<>();
        claims.put("ROLE", appUser.getRole().toString());
        Date now = new Date(System.currentTimeMillis());

        // Assign data to JWT
        JwtBuilder builder = Jwts.builder()
            .setId(id)
            .setSubject(subject)
            .setClaims(claims)
            .setIssuer(issuer)
            .setExpiration(exp)
            .setIssuedAt(now)
            .signWith(signatureAlgorithm, secretKeyBytes);

        return builder.compact();
    }

    public String decode(String token) {
        return null;
    }
}
