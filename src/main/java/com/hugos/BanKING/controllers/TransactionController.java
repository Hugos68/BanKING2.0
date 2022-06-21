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
@RequestMapping(path = "api/app-users/{email}/transactions")
public class TransactionController {
    private final ResourceProtectionService resourceProtectionService;

    @PostMapping(path = "/{type}")
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @PathVariable String email, @PathVariable String type) {
        log.info("Endpoint: POST \"api/app-users/{}/transactions?type={}\" was called", email, type);
        return resourceProtectionService.createTransaction(request, email, type.toUpperCase());
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(HttpServletRequest request, @PathVariable String email, @RequestParam(required = false) String sortBy) {
        log.info("Endpoint: GET \"api/app-users/{}/transactions?sortBy={}\" was called", email, sortBy);
        return resourceProtectionService.getTransactions(request, email, sortBy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(HttpServletRequest request, @PathVariable String email,@PathVariable Long id) {
        log.info("Endpoint: PUT \"api/app-users/{}/transactions/{}\" was called", email, id);
        return resourceProtectionService.updateTransaction(request, email, id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTransactions(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: DELETE \"api/app-user/{}/transactions\" was called", email);
        return resourceProtectionService.deleteTransactions(request, email);
    }
}
