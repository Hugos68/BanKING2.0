package com.hugos.BanKING.services;

import com.hugos.BanKING.entities.BankAccount;
import com.hugos.BanKING.enums.Role;
import com.hugos.BanKING.repositories.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

// This class is here to make sure any user trying to access a resource actually has access to that resource
// This verification process includes: JWT verification and Role validation

@Service
@RequiredArgsConstructor
public class ResourceProtectionService {

    private final TokenService tokenService;
    private final AppUserService appUserService;
    private final RequestService requestService;
    private final TransactionService transactionService;
    private final BankAccountRepository bankAccountRepository;

    public ResponseEntity<?> getAppUser(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER, email);
        return appUserService.getAppUser(email);
    }

    public ResponseEntity<?> createAppUser(HttpServletRequest request) {
        return appUserService.createAppUser(request);
    }

    public ResponseEntity<?> updateAppUser(HttpServletRequest request, String email) {
        return appUserService.updateAppUser(request, email);
    }

    public ResponseEntity<?> deleteAppUser(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER, email);
        return appUserService.deleteAppUser(email);
    }

    public ResponseEntity<?> authenticateAppUser(HttpServletRequest request) {
        return appUserService.authenticateAppUser(request);
    }

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String iban, String type) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByIban(iban);
        if (bankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account with iban not found");
        }
        String email = bankAccountOptional.get().getAppUser().getEmail();
        requestService.authorizeRequest(request, Role.USER, email);
        return transactionService.createTransaction(request, email, type);
    }

    public ResponseEntity<?> getTransactions(HttpServletRequest request, String iban, int limit, String sortBy) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByIban(iban);
        if (bankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account with iban not found");
        }
        String email = bankAccountOptional.get().getAppUser().getEmail();
        requestService.authorizeRequest(request, Role.USER, email);
        return transactionService.getTransactions(iban, limit, sortBy) ;
    }

    public ResponseEntity<?> updateTransaction(HttpServletRequest request, String iban, Long id) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByIban(iban);
        if (bankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account with iban not found");
        }
        String email = bankAccountOptional.get().getAppUser().getEmail();
        requestService.authorizeRequest(request, Role.ADMIN, email);
        return transactionService.updateTransaction(request, id);
    }

    public ResponseEntity<?> deleteTransactions(HttpServletRequest request, String iban) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByIban(iban);
        if (bankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account with iban not found");
        }
        String email = bankAccountOptional.get().getAppUser().getEmail();
        requestService.authorizeRequest(request, Role.USER, email);
        return transactionService.deleteTransactions(iban);
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return tokenService.refreshAccessToken(request);
    }
}