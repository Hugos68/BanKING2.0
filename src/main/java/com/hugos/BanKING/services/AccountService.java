package com.hugos.BanKING.services;

import com.hugos.BanKING.helpobjects.AuthorizationOutcome;
import com.hugos.BanKING.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AppUserService appUserService;
    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;
    private final AuthorizationService authorizationService;

    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        authorizeRequest(request);

        // Execute request once authorized
        return appUserService.getAccount(request);
    }
    public ResponseEntity<?> createAccount(HttpServletRequest request) {
        return appUserService.createAccount(request);
    }

    public ResponseEntity<?> updateAccount(HttpServletRequest request) {
        authorizeRequest(request);

        // Execute request once authorized
        return appUserService.updateAccount(request);
    }

    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        authorizeRequest(request);

        // Execute request once authorized
        return appUserService.deleteAccount(request);
    }

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String type) {
        authorizeRequest(request);

        // Execute request once authorized
        return transactionService.createTransaction(request, type);
    }

    public ResponseEntity<?> getTransactions(HttpServletRequest request) {
        authorizeRequest(request);

        // Execute request once authorized
        return transactionService.getTransactions(request);
    }

    public ResponseEntity<?> deleteTransactions(HttpServletRequest request) {
        authorizeRequest(request);

        // Execute request once authorized
        return transactionService.deleteAllTransactions(request);
    }

    private void authorizeRequest(HttpServletRequest request) {

        // Authorize access to this resource
        AuthorizationOutcome authorizationOutcome = authorizationService.authorizeAccessToken(request);
        if (!authorizationOutcome.isAuthorized() || !authorizationOutcome.getRole().equals(Role.USER)) {

            // Create authorization response
            throw new ResponseStatusException(authorizationOutcome.getStatus(), authorizationOutcome.getMessage());
        }
    }

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        return appUserService.authenticate(request);
    }

    public ResponseEntity<?> putBankAccount(HttpServletRequest request) {
        return bankAccountService.putBankAccount(request);
    }
}
