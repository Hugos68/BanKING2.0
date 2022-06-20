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

    private final TokenService tokenService;
    private final AppUserService appUserService;
    private final RequestService requestService;
    private final TransactionService transactionService;

    public ResponseEntity<?> getAppUser(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return appUserService.getAppUser(request, email);
    }

    public ResponseEntity<?> createAppUser(HttpServletRequest request) {
        return appUserService.createAppUser(request);
    }

    public ResponseEntity<?> updateAppUser(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return appUserService.updateAppUser(request);
    }

    public ResponseEntity<?> deleteAppUser(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return appUserService.deleteAppUser(request, email);
    }

    public ResponseEntity<?> authenticateAppUser(HttpServletRequest request) {
        return appUserService.authenticateAppUser(request);
    }

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String type) {
        requestService.authorizeRequest(request, Role.USER);
        return transactionService.createTransaction(request, type);
    }

    public ResponseEntity<?> getTransactions(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return transactionService.getTransactions(request, email);
    }

    public ResponseEntity<?> updateTransaction(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return transactionService.updateTransaction(request, email);
    }

    public ResponseEntity<?> deleteTransactions(HttpServletRequest request, String email) {
        requestService.authorizeRequest(request, Role.USER);
        return transactionService.deleteTransactions(request, email);
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return tokenService.refreshAccessToken(request);
    }

}
