package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.enums.TransactionType;
import com.hugos.BanKING.models.AuthorizationOutcome;
import com.hugos.BanKING.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AppUserService appUserService;
    private final BankAccountService bankAccountService;
    private final AuthorizationService authorizationService;

    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return appUserService.getAccount(request);
    }

    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return appUserService.deleteAccount(request);
    }

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String type) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.createTransaction(request, type);
    }

    public ResponseEntity<?> getAllTransactions(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.getAllTransactions(request);
    }

    private ResponseEntity<?> authorizeRequest(HttpServletRequest request) {

        // Authorize access to this resource
        AuthorizationOutcome authorizationOutcome = authorizationService.authorizeAccessToken(request);
        if (!authorizationOutcome.isAuthorized() || !authorizationOutcome.getRole().equals(Role.USER)) {

            // Create authorization response
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", authorizationOutcome.getMessage());
            return ResponseEntity.status(authorizationOutcome.getStatus()).body(jsonObject.toString());
        }

        // Return empty response entity if request was authorized
        return null;
    }

}
