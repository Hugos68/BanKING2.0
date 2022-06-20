package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.ResourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/transaction")
@AllArgsConstructor
public class TransactionController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @RequestParam String type) {
        log.info("Endpoint: POST \"api/transaction\" was called");
        return resourceService.createTransaction(request, type);
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(HttpServletRequest request) {
        log.info("Endpoint: GET \"api/transaction\" was called");
        return resourceService.getTransactions(request);
    }

    @PutMapping ResponseEntity<?> updateTransaction(HttpServletRequest request) {
        log.info("Endpoint: PUT \"api/transaction\" was called");
        return resourceService.updateTransaction(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTransactions(HttpServletRequest request) {
        log.info("Endpoint: DELETE \"api/transaction\" was called");
        return resourceService.deleteTransactions(request);
    }
}
