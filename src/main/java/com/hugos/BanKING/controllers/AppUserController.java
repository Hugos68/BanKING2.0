package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.ResourceService;
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
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createAppUser(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/app-user\" was called");
        return resourceService.createAppUser(request);
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> getAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: GET \"api/app-users/{}\" was called", email);
        return resourceService.getAppUser(request, email);
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<?> updateAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: PUT \"api/app-users/{}\" was called", email);
        return resourceService.updateAppUser(request, email);
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<?> deleteAppUser(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: DELETE \"api/app-users/{}\" was called", email);
        return resourceService.deleteAppUser(request, email);
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/app-users/authentication\" was called");
        return resourceService.authenticateAppUser(request);
    }
}