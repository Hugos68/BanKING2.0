package com.hugos.BanKING.account;


import com.hugos.BanKING.appuser.AppUserService;
import com.hugos.BanKING.bankaccount.BankAccountService;
import com.hugos.BanKING.util.AuthorizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(path = "api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AppUserService appUserService;
    private final BankAccountService bankAccountService;
    private final AuthorizeService authorizeService;

    @GetMapping(path = "/email")
    public ResponseEntity<?> getEmail(HttpServletRequest request) {

        // Verify token from header
        if (!authorize(request)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return appUserService.getEmail(request);
    }

    @GetMapping(path = "/bankaccount")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {

        // Verify token from header
        if (!authorize(request)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return appUserService.getBankAccount(request);
    }

    @PostMapping(path = "deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request) {

        // Verify token from header
        if (!authorize(request)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return bankAccountService.deposit(request);
    }

    @PostMapping(path = "withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {

        // Verify token from header
        if (!authorize(request)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return bankAccountService.withdraw(request);
    }

    @PostMapping(path = "transfer")
    public ResponseEntity<?> transfer(HttpServletRequest request) {

        // Verify token from header
        if (!authorize(request)) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return bankAccountService.transfer(request);
    }

    private boolean authorize(HttpServletRequest request) {
        return authorizeService.isAuthorized(request.getHeader(AUTHORIZATION).substring("Bearer ".length()));
    }
}
