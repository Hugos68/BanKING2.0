package com.hugos.BanKING.services;

import com.google.gson.*;
import com.hugos.BanKING.enums.TransactionType;
import com.hugos.BanKING.models.AppUser;
import com.hugos.BanKING.models.BankAccount;
import com.hugos.BanKING.models.DecodedAccessToken;
import com.hugos.BanKING.models.Transaction;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.repositories.TransactionRepository;
import com.hugos.BanKING.util.RequestUtility;
import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class BankAccountService {

    // TODO: Add business logic for transfer

    private final TransactionRepository transactionRepository;
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

        // Log transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.DEPOSIT,
            null,
            bankAccount,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

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

        // Log transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.WITHDRAW,
            bankAccount,
            null,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        // Create json response body
        jsonObject.addProperty("message", "Amount withdrawn");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {

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
        String receiverIban = body.get("iban").toString();
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            // Create json response body
            jsonObject.addProperty("message", "Invalid amount");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }


        // Check if bank account with that iban exists
        Optional<BankAccount> receiverOptional = bankAccountRepository.findByIban(
            receiverIban
        );
        if (receiverOptional.isEmpty()) {
            // Create json response body
            jsonObject.addProperty("message", "Iban does not exist");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Get receiver bank account
        BankAccount receiverBankAccount = receiverOptional.get();

        // Get sender bank account
        BankAccount senderBankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(
                decodedAccessToken.subject()
            ).get()
        ).get();

        // Get require info from accounts
        String senderIban = senderBankAccount.getIban();
        double senderBalance = senderBankAccount.getBalance();
        double receiverBalance = receiverBankAccount.getBalance();

        if (senderBalance - amount < 0) {
            // Create json response body
            jsonObject.addProperty("message", "Insufficient balance");

            // Return response
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Subtract amount from sender bank account
        senderBankAccount.setBalance(senderBalance-amount);
        bankAccountRepository.save(senderBankAccount);

        // Add amount to receiver bank account
        receiverBankAccount.setBalance(receiverBalance+amount);
        bankAccountRepository.save(receiverBankAccount);

        // Log transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.TRANSFER,
            senderBankAccount,
            receiverBankAccount,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        // Create json response body
        jsonObject.addProperty("message", "Amount transferred");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> getAllTransactions(HttpServletRequest request) {

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

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
                appUserRepository.findByEmail(decodedAccessToken.subject()).get()
        ).get();

        // Get json object from relevant transactions
        JsonObject transactionObject = new JsonObject();
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            if (bankAccount!=transaction.getFromBankAccount() && bankAccount!=transaction.getToBankAccount()) {
                transactionList.remove(transaction);
                return;
            }
            transactionObject.addProperty("transaction_"+transaction.getId(), transaction.toString());
        });

        // Create json response body
        jsonObject.addProperty("message", "Transactions retrieved");
        jsonObject.add("transactions", transactionObject);

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

}
