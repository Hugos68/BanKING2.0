package com.hugos.BanKING.services;

import com.hugos.BanKING.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

// This class is here to make sure any user trying to access a resource actually has access to that resource
// This verification process includes: JWT verification and Role validation

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final JwtService jwtService;
    private final AppUserService appUserService;
    private final TransactionService transactionService;
    private final AuthorizationService authorizationService;

    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.USER);
        return appUserService.getAccount(request);
    }

    public ResponseEntity<?> createAccount(HttpServletRequest request) {
        return appUserService.createAccount(request);
    }

    public ResponseEntity<?> updateAccount(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.USER);
        return appUserService.updateAccount(request);
    }

    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.USER);
        return appUserService.deleteAccount(request);
    }

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        return appUserService.authenticate(request);
    }

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String type) {
        authorizationService.authorizeRequest(request, Role.USER);
        return transactionService.createTransaction(request, type);
    }

    public ResponseEntity<?> getTransactions(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.USER);
        return transactionService.getTransactions(request);
    }

    public ResponseEntity<?> updateTransaction(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.ADMIN);
        return transactionService.updateTransaction(request);
    }

    public ResponseEntity<?> deleteTransactions(HttpServletRequest request) {
        authorizationService.authorizeRequest(request, Role.USER);
        return transactionService.deleteAllTransactions(request);
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return jwtService.refreshAccessToken(request);
    }
}
