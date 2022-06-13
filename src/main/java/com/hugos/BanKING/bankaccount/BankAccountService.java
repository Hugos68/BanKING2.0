package com.hugos.BanKING.bankaccount;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> findByIban(String iban) {
        return bankAccountRepository.findByIban(iban);
    }

    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

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
