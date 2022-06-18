package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hugos.BanKING.models.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final JwtService jwtService;

    // Returns null if parse fails
    public JsonObject getJsonFromRequest(HttpServletRequest request) {
        JsonObject jsonObj;
        try {
             jsonObj = JsonParser.parseString(request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))).getAsJsonObject();
             return jsonObj;
        } catch (IOException e) {
            return null;
        }
    }

    // Returns null if parse fails
    public DecodedAccessToken getDecodedAccessTokenFromRequest(HttpServletRequest request) {
        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }
        return jwtService.decodeAccessToken(accessToken);
    }
}