package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/accesstoken")
@AllArgsConstructor
public class AccessTokenController {
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getAccessToken(HttpServletRequest request) {
        log.info("Endpoint: GET \"api/accesstoken\" was called");
        return jwtService.refreshAccessToken(request);
    }
}