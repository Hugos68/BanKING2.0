package com.hugos.BanKING.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "api/account")
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account\" was called");
        return accountService.getAccount(request);
    }

    @PostMapping(path = "deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/deposit\" was called");
        return accountService.deposit(request);
    }

    @PostMapping(path = "withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/withdraw\" was called");
        return accountService.withdraw(request);
    }

    @PostMapping(path = "transfer")
    public ResponseEntity<?> transfer(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/transfer\" was called");
        return accountService.transfer(request);
    }
}