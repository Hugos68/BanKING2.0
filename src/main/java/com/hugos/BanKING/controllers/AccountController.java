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
@RequestMapping(path = "api/account")
public class AccountController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createAccount(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/account\" was called");
        return resourceService.createAccount(request);
    }

    @GetMapping
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        log.info("Endpoint: GET \"api/account\" was called");
        return resourceService.getAccount(request);
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(HttpServletRequest request) {
        log.info("Endpoint: PUT \"api/account\" was called");
        return resourceService.updateAccount(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        log.info("Endpoint: DELETE \"api/account\" was called");
        return resourceService.deleteAccount(request);
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: POST \"api/account/authentication\" was called");
        return resourceService.authenticate(request);
    }


}