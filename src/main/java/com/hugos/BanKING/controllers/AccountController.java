package com.hugos.BanKING.controllers;

import com.hugos.BanKING.enums.TransactionType;
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

    @GetMapping
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.getAccount(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.deleteAccount(request);
    }

    @PostMapping(path = "transaction")
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @RequestParam String type) {
        log.info("Endpoint: \"api/transaction\" was called");
        return accountService.createTransaction(request, type);
    }

    @GetMapping(path = "transactions")
    public ResponseEntity<?> getAllTransactions(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/transaction\" was called");
        return accountService.getAllTransactions(request);
    }
}