package com.hugos.BanKING2.controllers;

import com.hugos.BanKING2.services.ResourceProtectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/access-token")
@AllArgsConstructor
public class AccessTokenController {

    private final ResourceProtectionService resourceProtectionService;

    @GetMapping
    public ResponseEntity<?> getAccessToken(HttpServletRequest request) {
        log.info("Endpoint: GET \"api/access-token\" was called");
        return resourceProtectionService.refreshAccessToken(request);
    }
}