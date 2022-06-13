package com.hugos.BanKING.util;

import com.hugos.BanKING.jwt.JwtService;
import com.hugos.BanKING.jwt.tokens.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizeService {

    private final JwtService jwtService;

    public boolean isAuthorized(String token) {
        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(token);
        if (decodedAccessToken==null) {
            return false;
        }
        if (decodedAccessToken.isExpired()) {
            return false;
        }
        return true;
    }

}
