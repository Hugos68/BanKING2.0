package com.hugos.BanKING.util;

import com.hugos.BanKING.jwt.JwtService;
import com.hugos.BanKING.jwt.tokens.DecodedAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizeService {

    private final JwtService jwtService;

    public DecodedAccessToken getDecodedAccessToken(String token) {
        return jwtService.decodeAccessToken(token);
    }

}
