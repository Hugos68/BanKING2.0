package com.hugos.BanKING.services;

import com.google.gson.*;
import com.hugos.BanKING.enums.TransactionType;
import com.hugos.BanKING.entities.BankAccount;
import com.hugos.BanKING.helpobjects.DecodedAccessToken;
import com.hugos.BanKING.entities.Transaction;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BankAccountService {
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AppUserRepository appUserRepository;
    private final RequestService requestService;

    public ResponseEntity<?> deposit(HttpServletRequest request) {

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid amount");
        }

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(decodedAccessToken.subject()).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (amount > 1000) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Transfer limit reached");
        }

        if (amount < 0.01) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid amount");
        }

        // Subtract amount from bank account
        bankAccount.setBalance(balance+amount);
        bankAccountRepository.save(bankAccount);

        // Create and save transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.DEPOSIT,
            null,
            bankAccount,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        // Log deposit
        log.info("User \"{}\" deposited {}", decodedAccessToken.subject(), amount);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Amount deposited");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String receiverIban = body.get("iban").toString().replace("\"", "");
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid amount");
        }

        // Check if bank account with that iban exists
        Optional<BankAccount> receiverOptional = bankAccountRepository.findByIban(
            receiverIban
        );

        if (receiverOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Iban does not exist");
        }

        // Get receiver bank account
        BankAccount receiverBankAccount = receiverOptional.get();

        // Get sender bank account
        BankAccount senderBankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(
                decodedAccessToken.subject()
            ).get()
        ).get();


        if(receiverBankAccount==senderBankAccount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot transfer to yourself");
        }

        // Get require info from accounts
        double senderBalance = senderBankAccount.getBalance();
        double receiverBalance = receiverBankAccount.getBalance();

        if (senderBalance - amount < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient balance");
        }

        // Subtract amount from sender bank account
        senderBankAccount.setBalance(senderBalance-amount);
        bankAccountRepository.save(senderBankAccount);

        // Add amount to receiver bank account
        receiverBankAccount.setBalance(receiverBalance+amount);
        bankAccountRepository.save(receiverBankAccount);

        // Create and save transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.TRANSFER,
            senderBankAccount,
            receiverBankAccount,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        // Log withdrawal
        log.info("User \"{}\" transferred {} to {}", decodedAccessToken.subject(), amount, receiverIban);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Amount transferred");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> withdraw(HttpServletRequest request) {

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString().replace("\"", ""));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid amount");
        }

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
            appUserRepository.findByEmail(
                    decodedAccessToken.subject()
            ).get()
        ).get();

        double balance = bankAccount.getBalance();

        if (balance - amount < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient balance");
        }

        // Subtract amount from bank account
        bankAccount.setBalance(balance-amount);
        bankAccountRepository.save(bankAccount);

        // Create and save transaction
        Transaction transaction = new Transaction(
            null,
            TransactionType.WITHDRAW,
            bankAccount,
            null,
            amount,
            LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        // Log withdrawal
        log.info("User \"{}\" withdrew {}", decodedAccessToken.subject(), amount);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Amount withdrawn");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }
}