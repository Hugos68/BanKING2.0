package com.hugos.BanKING2.controllers;

import com.hugos.BanKING2.services.ResourceProtectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/app-users")
public class AppUserController {

    private final ResourceProtectionService resourceProtectionService;

    @PostMapping
    public ResponseEntity<?> createAppUser(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/app-user\" was called");
        return resourceProtectionService.createAppUser(request);
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> getAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: GET \"api/app-users/{}\" was called", email);
        return resourceProtectionService.getAppUser(request, email);
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<?> updateAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: PUT \"api/app-users/{}\" was called", email);
        return resourceProtectionService.updateAppUser(request, email);
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<?> deleteAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: DELETE \"api/app-users/{}\" was called", email);
        return resourceProtectionService.deleteAppUser(request, email);
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/app-users/authentication\" was called");
        return resourceProtectionService.authenticateAppUser(request);
    }
}