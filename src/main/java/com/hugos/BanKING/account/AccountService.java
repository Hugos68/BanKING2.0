package com.hugos.BanKING.account;

import com.google.gson.Gson;
import com.hugos.BanKING.appuser.AppUserService;
import com.hugos.BanKING.authorization.AuthorizationOutcome;
import com.hugos.BanKING.bankaccount.BankAccountService;
import com.hugos.BanKING.authorization.AuthorizationService;
import com.hugos.BanKING.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AppUserService appUserService;
    private final BankAccountService bankAccountService;
    private final AuthorizationService authorizationService;

    public ResponseEntity<?> getEmail(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return appUserService.getEmail(request);
    }


    public ResponseEntity<?> getBalance(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.getBalance(request);
    }

    public ResponseEntity<?> deposit(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.deposit(request);
    }

    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.withdraw(request);
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {
        ResponseEntity<?> authorizeResponse = authorizeRequest(request);

        // If response entity is not null, request was unauthorized
        if (authorizeResponse!=null) {
            return authorizeResponse;
        }

        // Execute request once authorized
        return bankAccountService.transfer(request);
    }

    private ResponseEntity<?> authorizeRequest(HttpServletRequest request) {

        // Authorize access to this resource
        AuthorizationOutcome authorizationOutcome = authorizationService.authorize(request);
        if (!authorizationOutcome.isAuthorized() || !authorizationOutcome.getRole().equals(Role.USER)) {

            Map<String, String> responseMap = new HashMap<>();

            HttpStatus status = authorizationOutcome.getStatus();
            String message = authorizationOutcome.getMessage();

            // Create json response body
            responseMap.put("message", message);
            String responseBody = new Gson().toJson(responseMap);

            // Compile authorization response
            return ResponseEntity.status(status).body(responseBody);
        }

        // Return empty response entity if request was authorized
        return null;
    }

}
