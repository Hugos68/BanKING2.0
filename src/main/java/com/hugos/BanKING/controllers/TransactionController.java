package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.ResourceService;
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
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @RequestParam String type) {
        log.info("Endpoint: POST \"api/transactions\" was called");
        return resourceService.createTransaction(request, type);
    }

    @GetMapping(path = "/app-users/{email}")
    public ResponseEntity<?> getTransactions(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: GET \"api/transactions/app-users/{}\" was called", email);
        return resourceService.getTransactions(request, email);
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<?> updateTransaction(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: GET \"api/transactions/{}\" was called", email);
        return resourceService.updateTransaction(request, email);
    }

    @DeleteMapping(path = "/user/{email}")
    public ResponseEntity<?> deleteTransactions(HttpServletRequest request, @PathVariable String email) {
        log.info("Endpoint: DELETE \"api/transactions/app-users/{}\" was called", email);
        return resourceService.deleteTransactions(request, email);
    }
}
