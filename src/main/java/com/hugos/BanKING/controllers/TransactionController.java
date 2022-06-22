package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.ResourceProtectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/bank-accounts/{iban}/transactions")
public class TransactionController {

    private final ResourceProtectionService resourceProtectionService;

    @PostMapping(path = "/{type}")
    public ResponseEntity<?> createTransaction(HttpServletRequest request,
                                               @PathVariable String iban,
                                               @PathVariable String type) {
        log.info("Endpoint: POST \"api/bank-account/{}/transactions?type={}\" was called", iban, type);
        return resourceProtectionService.createTransaction(request, iban, type.toUpperCase());
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(HttpServletRequest request,
                                             @PathVariable String iban,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) Integer limit) {
        log.info("Endpoint: GET \"api/bank-account/{}/transactions?limit={}&sortBy={}\" was called", iban, limit, sortBy);
        if (limit==null || limit < 0) limit = 0;
        return resourceProtectionService.getTransactions(request, iban, limit, sortBy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(HttpServletRequest request,
                                               @PathVariable String iban,
                                               @PathVariable Long id) {
        log.info("Endpoint: PUT \"api/bank-account/{}/transactions/{}\" was called", iban, id);
        return resourceProtectionService.updateTransaction(request, iban, id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTransactions(HttpServletRequest request, @PathVariable String iban) {
        log.info("Endpoint: DELETE \"api/bank-account/{}/transactions\" was called", iban);
        return resourceProtectionService.deleteTransactions(request, iban);
    }
}