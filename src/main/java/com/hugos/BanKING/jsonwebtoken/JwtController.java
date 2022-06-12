package com.hugos.BanKING.jsonwebtoken;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/token/refresh")
@AllArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return jwtService.refresh(request);
    }
}
