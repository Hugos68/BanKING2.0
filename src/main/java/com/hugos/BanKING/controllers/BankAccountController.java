package com.hugos.BanKING.controllers;

import com.hugos.BanKING.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/bankaccount")
public class BankAccountController {
    private final AccountService accountService;

    @PutMapping
    public ResponseEntity<?> putBankAccount(HttpServletRequest request) {
        return accountService.putBankAccount(request);
    }
}
