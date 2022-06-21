package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hugos.BanKING.enums.Role;
import com.hugos.BanKING.util.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final TokenService tokenService;

    public JsonObject getJsonFromRequest(HttpServletRequest request) {
        JsonObject jsonObj;
        try {
            jsonObj = JsonParser.parseString(request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))).getAsJsonObject();
            return jsonObj;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unable to process request body");
        }
    }

    public DecodedAccessToken getDecodedAccessTokenFromRequest(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        return tokenService.decodeAccessToken(accessToken);
    }

    public void authorizeRequest(HttpServletRequest request, Role requiredRole, String email) {

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing access token");
        }

        DecodedAccessToken decodedAccessToken = tokenService.decodeAccessToken(accessToken);

        // Check if token is expired
        if (decodedAccessToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token is invalid");
        }

        // Check if the role from the token has the minimum level of clearance
        if (!decodedAccessToken.subject().equals(email) &&
            decodedAccessToken.role().getLevelOfClearance() < requiredRole.getLevelOfClearance()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized");
        }
    }
}