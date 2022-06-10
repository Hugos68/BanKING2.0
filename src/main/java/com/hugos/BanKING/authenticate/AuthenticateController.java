package com.hugos.BanKING.authenticate;

import com.hugos.BanKING.util.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/authenticate")
@RequiredArgsConstructor
public class AuthenticateController {

    @GetMapping
    public ResponseEntity<?> authenticate(AuthenticateRequest authenticateRequest) {
        return null;
    }
}
