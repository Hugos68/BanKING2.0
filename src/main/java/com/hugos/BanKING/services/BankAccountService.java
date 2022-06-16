package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.models.BankAccount;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.util.RequestUtility;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@AllArgsConstructor
public class BankAccountService {

    // TODO: Add business logic for deposit, withdraw and transfer

    private final BankAccountRepository bankAccountRepository;
    private final AppUserRepository appUserRepository;
    private final RequestUtility requestUtility;

    public ResponseEntity<?> deposit(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        String email = body.get("subject").getAsString();
        double amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));

        // Create response object
        JsonObject jsonObject = new JsonObject();

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(email).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (amount > 10000) {
            // Create json response body
            jsonObject.addProperty("message", "Transfer limit reached");

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

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        String email = body.get("subject").getAsString();
        double amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));

        // Create response object
        JsonObject jsonObject = new JsonObject();

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(email).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (balance - amount < 0) {
            // Create json response body
            jsonObject.addProperty("message", "Balance can not be negative");

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
