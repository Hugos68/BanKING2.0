package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.entities.AppUser;
import com.hugos.BanKING.entities.BankAccount;
import com.hugos.BanKING.entities.Transaction;
import com.hugos.BanKING.enums.TransactionType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {

    private final BankAccountService bankAccountService;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final AppUserRepository appUserRepository;

    public ResponseEntity<?> createTransaction(HttpServletRequest request, String email, String type) {

        // Execute request once authorized
        if (type.equals(TransactionType.DEPOSIT.name())) {
            return bankAccountService.deposit(request, email);
        }
        if (type.equals(TransactionType.TRANSFER.name())) {
            return bankAccountService.transfer(request, email);
        }
        if (type.equals(TransactionType.WITHDRAW.name())) {
            return bankAccountService.withdraw(request, email);
        }

        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown transaction type");
    }

    public ResponseEntity<?> getTransactions(String email, String sortBy) {

        if (sortBy==null) {

        }

        // This checks if the given email is an existing user
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        AppUser appUser = optionalAppUser.get();
        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // Get relevant transactions
        JsonObject transactionsObject = new JsonObject();
        Optional<List<Transaction>> optionalFromList = transactionRepository.findAllByFromBankAccount(bankAccount);
        Optional<List<Transaction>> optionalToList = transactionRepository.findAllByToBankAccount(bankAccount);

        List<Transaction> transactionList = new ArrayList<>();
        optionalFromList.ifPresent(transactionList::addAll);
        optionalToList.ifPresent(transactionList::addAll);

        // Create json object from transactions
        transactionList.forEach(transaction -> {
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
        log.info("Transactions from user \"{}\" were fetched", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions retrieved");
        jsonObject.add("transactions", transactionsObject);

        // Return response
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }

    public ResponseEntity<?> deleteTransactions(String email) {

        // This checks if the given email is an existing user
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        AppUser appUser = optionalAppUser.get();
        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // Clear all transactions from user
        transactionRepository.deleteAllByFromBankAccount(bankAccount);
        transactionRepository.deleteAllByToBankAccount(bankAccount);

        // Log deletion
        log.info("Transactions from user \"{}\" were deleted", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions deleted");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }
}
