package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.models.AppUser;
import com.hugos.BanKING.models.BankAccount;
import com.hugos.BanKING.models.DecodedAccessToken;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.util.RequestUtility;
import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class BankAccountService {

    // TODO: Add business logic for transfer

    private final BankAccountRepository bankAccountRepository;
    private final AppUserRepository appUserRepository;
    private final RequestUtility requestUtility;
    private final JwtService jwtService;

    public ResponseEntity<?> deposit(HttpServletRequest request) {

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            // Create json response body
            jsonObject.addProperty("message", "Invalid amount");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(decodedAccessToken.subject()).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (amount > 1000) {
            // Create json response body
            jsonObject.addProperty("message", "Transfer limit reached");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        if (amount < 0.01) {
            // Create json response body
            jsonObject.addProperty("message", "Invalid amount");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Subtract amount from bank account
        bankAccount.setBalance(balance+amount);
        bankAccountRepository.save(bankAccount);

        // Create json response body
        jsonObject.addProperty("message", "Amount deposited");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> withdraw(HttpServletRequest request) {

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            // Create json response body
            jsonObject.addProperty("message", "Invalid amount");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(
                    decodedAccessToken.subject()
            ).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (balance - amount < 0) {
            // Create json response body
            jsonObject.addProperty("message", "Insufficient balance");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Subtract amount from bank account
        bankAccount.setBalance(balance-amount);
        bankAccountRepository.save(bankAccount);

        // Create json response body
        jsonObject.addProperty("message", "Amount withdrawn");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {
        return null;
    }

}
