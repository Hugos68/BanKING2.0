package com.hugos.BanKING.account;

import com.hugos.BanKING.appuser.AppUserService;
import com.hugos.BanKING.bankaccount.BankAccountService;
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

    private final AppUserService appUserService;
    private final BankAccountService bankAccountService;

    // TODO: Create logic that makes sure access token is valid, not expired and contains roles to access resources

    @GetMapping(path = "/email")
    public ResponseEntity<?> getEmail(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/email\" was called");
        return appUserService.getEmail(request);
    }

    @GetMapping(path = "/bankaccount")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/bankaccount\" was called");
        return appUserService.getBankAccount(request);
    }

    @PostMapping(path = "deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/deposit\" was called");
        return bankAccountService.deposit(request);
    }

    @PostMapping(path = "withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/withdraw\" was called");
        return bankAccountService.withdraw(request);
    }

    @PostMapping(path = "transfer")
    public ResponseEntity<?> transfer(HttpServletRequest request) {
        log.info("Endpoint: \"api/account/transfer\" was called");
        return bankAccountService.transfer(request);
    }
}