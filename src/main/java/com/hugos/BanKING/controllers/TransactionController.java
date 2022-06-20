package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/transactions")
@AllArgsConstructor
public class TransactionController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> postTransactions(HttpServletRequest request, @RequestParam String type) {
        log.info("Endpoint: \"api/transaction\" was called");
        return accountService.createTransaction(request, type);
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/transaction\" was called");
        return accountService.getTransactions(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTransactions(HttpServletRequest request) {
        log.info("Endpoint: \"api/transaction\" was called");
        return accountService.deleteTransactions(request);
    }
}