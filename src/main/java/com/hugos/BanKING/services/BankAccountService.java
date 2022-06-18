package com.hugos.BanKING.services;

import com.google.gson.*;
import com.hugos.BanKING.enums.TransactionType;
import com.hugos.BanKING.models.BankAccount;
import com.hugos.BanKING.models.DecodedAccessToken;
import com.hugos.BanKING.models.Transaction;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

        // Create response object
        JsonObject jsonObject = new JsonObject();

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
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

        // Log deposit
        log.info("User \"{}\" deposited {}", decodedAccessToken.subject(), amount);

        // Create json response body
        jsonObject.addProperty("message", "Amount deposited");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> transfer(HttpServletRequest request) {

        // Create response object
        JsonObject jsonObject = new JsonObject();

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String receiverIban = body.get("iban").toString().replace("\"", "");
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


        if(receiverBankAccount==senderBankAccount) {
            // Create json response body
            jsonObject.addProperty("message", "Cannot transfer to yourself");

            // Return response
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonObject.toString());
        }

        // Get require info from accounts
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

        // Save transaction in database
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
        jsonObject.addProperty("message", "Amount transferred");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> withdraw(HttpServletRequest request) {

        // Create response object
        JsonObject jsonObject = new JsonObject();
        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
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

        // Log withdrawal
        log.info("User \"{}\" withdrew {}", decodedAccessToken.subject(), amount);

        // Create json response body
        jsonObject.addProperty("message", "Amount withdrawn");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> getAllTransactions(HttpServletRequest request) {

        // Create response object
        JsonObject jsonObject = new JsonObject();

        DecodedAccessToken decodedAccessToken = requestService.getDecodedAccessTokenFromRequest(request);

        BankAccount bankAccount = bankAccountRepository.findByAppUser(
                appUserRepository.findByEmail(decodedAccessToken.subject()).get()
        ).get();

        // Get json object from relevant transactions
        JsonObject transactionsObject = new JsonObject();
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            if (bankAccount!=transaction.getFromBankAccount() && bankAccount!=transaction.getToBankAccount()) {
                transactionList.remove(transaction);
                return;
            }
            JsonObject transactionObject = new JsonObject();
            String ibanFrom;
            if (transaction.getFromBankAccount()==null) {
                ibanFrom="unknown source";
            }
            else {
                ibanFrom = transaction.getFromBankAccount().getIban();
            }
            String ibanTo;
            if (transaction.getToBankAccount()==null) {
                ibanTo = "unknown source";
            }
            else {
                ibanTo = transaction.getToBankAccount().getIban();
            }
            transactionObject.addProperty("id", transaction.getId());
            transactionObject.addProperty("type", transaction.getType().name());
            transactionObject.addProperty("iban_from", ibanFrom);
            transactionObject.addProperty("iban_to", ibanTo);
            transactionObject.addProperty("amount", transaction.getAmount());
            transactionObject.addProperty("date_time", transaction.getDateTime().toString());
            transactionsObject.add("transaction_"+transaction.getId(), transactionObject);
        });

        // Log fetch
        log.info("Transactions from user \"{}\" were fetched", decodedAccessToken.subject());


        // Create json response body
        jsonObject.addProperty("message", "Transactions retrieved");
        jsonObject.add("transactions", transactionsObject);

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }
}