package com.hugos.BanKING.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping(path = "/email")
    public ResponseEntity<?> getEmail(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/email\" was called");
        return accountService.getEmail(request);
    }

    @GetMapping(path = "/bankaccount")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/bankaccount\" was called");
        return accountService.getBankAccount(request);
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