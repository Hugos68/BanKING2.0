package com.hugos.BanKING.services;

import com.hugos.BanKING.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Service
@RequiredArgsConstructor
public class JwtServiceHandler {

    private final JwtService jwtService;

    public String getBearerEmail(HttpServletRequest request, TokenType tokenType) {
        String token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());

        if (tokenType.equals(TokenType.ACCESS)) {
           return jwtService.decodeAccessToken(token).subject();
        }

        return jwtService.decodeRefreshToken(token).subject();
    }
}
