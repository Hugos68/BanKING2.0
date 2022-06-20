package com.hugos.BanKING.services;

import com.hugos.BanKING.enums.Role;
import com.hugos.BanKING.helpobjects.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final JwtService jwtService;

    public void authorizeRequest(HttpServletRequest request, Role minimumRole) {

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing JWT in header");
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        // Create outcome object based on token properties
        if (decodedAccessToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Access token is invalid");
        }
        if (decodedAccessToken.role().getLevelOfClearance() < minimumRole.getLevelOfClearance()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized");
        }
    }
}