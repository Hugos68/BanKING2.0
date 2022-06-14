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
public class AccountController {

    private final AccountService accountService;

    @CrossOrigin
    @GetMapping(path = "/email")
    public ResponseEntity<?> getEmail(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/email\" was called");
        return accountService.getEmail(request);
    }

    @CrossOrigin
    @GetMapping(path = "/balance")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/bankaccount\" was called");
        return accountService.getBalance(request);
    }

    @CrossOrigin
    @PostMapping(path = "deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/deposit\" was called");
        return accountService.deposit(request);
    }

    @CrossOrigin
    @PostMapping(path = "withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/withdraw\" was called");
        return accountService.withdraw(request);
    }

    @CrossOrigin
    @PostMapping(path = "transfer")
    public ResponseEntity<?> transfer(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/transfer\" was called");
        return accountService.transfer(request);
    }
}