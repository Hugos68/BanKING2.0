package com.hugos.BanKING.bankaccount;

import com.google.gson.JsonObject;
import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.appuser.AppUserService;
import com.hugos.BanKING.jwt.JwtServiceHandler;
import com.hugos.BanKING.jwt.tokens.TokenType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BankAccountService {

    // TODO: Add business logic for deposit, withdraw and transfer

    private final BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> findByIban(String iban) {
        return bankAccountRepository.findByIban(iban);
    }

    public Optional<BankAccount> findByAppUser(AppUser appUser) {
        return bankAccountRepository.findByAppUser(appUser);
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
