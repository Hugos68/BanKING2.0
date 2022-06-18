package com.hugos.BanKING.services;

import com.hugos.BanKING.models.AuthorizationOutcome;
import com.hugos.BanKING.models.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final JwtService jwtService;
    private final AppUserService appUserService;

    public AuthorizationOutcome authorizeAccessToken(HttpServletRequest request) {

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        // Create outcome object based on token properties
        if (decodedAccessToken==null) {
            return new AuthorizationOutcome(
                false,
                null,
                null,
                UNAUTHORIZED,
                "Access token is invalid"
            );
        }
        if (decodedAccessToken.isExpired()) {
            return new AuthorizationOutcome(
                false,
                decodedAccessToken.subject(),
                null,
                UNAUTHORIZED,
                "Access token is invalid"
            );
        }
        if (appUserService.findByEmail(decodedAccessToken.subject()).isEmpty()) {
            return new AuthorizationOutcome(
                false,
                decodedAccessToken.subject(),
                null,
                UNAUTHORIZED,
                "Access token is invalid"
            );
        }
        return new AuthorizationOutcome(
            true,
            decodedAccessToken.subject(),
            decodedAccessToken.role(),
            OK,
            "Access token was validated"
        );
    }
}