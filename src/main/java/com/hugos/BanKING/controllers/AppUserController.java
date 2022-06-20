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
@RequestMapping(path = "api/apppuser")
public class AppUserController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createAppUser(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/appuser\" was called");
        return resourceService.createAppUser(request);
    }

    @GetMapping
    public ResponseEntity<?> getAppUser(HttpServletRequest request) {
        log.info("Endpoint: GET \"api/appuser\" was called");
        return resourceService.getAppUser(request);
    }

    @PutMapping
    public ResponseEntity<?> updateAppUser(HttpServletRequest request) {
        log.info("Endpoint: PUT \"api/appuser\" was called");
        return resourceService.updateAppUser(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAppUser(HttpServletRequest request) {
        log.info("Endpoint: DELETE \"api/appuser\" was called");
        return resourceService.deleteAppUser(request);
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/appuser/authentication\" was called");
        return resourceService.authenticateAppUser(request);
    }


}