package com.hugos.BanKING.services;

import com.hugos.BanKING.repositories.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class BankAccountService {

    // TODO: Add business logic for deposit, withdraw and transfer

    private final BankAccountRepository bankAccountRepository;
    public ResponseEntity<?> deposit(HttpServletRequest request) {
        return null;
    }

    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        return null;
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {
        return null;
    }

}
