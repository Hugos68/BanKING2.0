package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.AccountService;
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
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.createAccount(request);
    }

    @GetMapping
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.getAccount(request);
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.updateAccount(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.deleteAccount(request);
    }

}