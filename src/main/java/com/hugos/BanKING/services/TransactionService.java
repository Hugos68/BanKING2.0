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
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {

    private final RequestService requestService;
    private final AppUserRepository appUserRepository;
    private final BankAccountService bankAccountService;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

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

        // Set default sorting algorithm by id
        if (sortBy==null || sortBy.equals("id")) {
            transactionList.sort(Comparator.comparingLong(Transaction::getId));
        }
        else if (sortBy.equals("timestamp")) {
            // TODO: Sort by date
        }
        else if (sortBy.equals("amount")) {
            transactionList.sort(Comparator.comparingDouble(Transaction::getAmount));
        }

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
            transactionObject.addProperty("date_time", transaction.getTimestamp().toString());
            transactionsObject.add("transaction_"+transaction.getId(), transactionObject);
        });

        // Log fetch
        log.info("Transactions from user \"{}\" were fetched", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions retrieved");
        jsonObject.add("transactions", transactionsObject);

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> updateTransaction(HttpServletRequest request, String email, Long id) {

        // This checks if the given email is an existing user
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        AppUser appUser = optionalAppUser.get();
        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // This checks if the given id corresponds to an existing transaction
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        Transaction transaction = optionalTransaction.get();

        // Get body from request
        JsonObject body = requestService.getJsonFromRequest(request);
        Optional<BankAccount> fromBankAccountOptional = bankAccountRepository.findByIban(body.get("from_iban").getAsString());
        if (fromBankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to set attribute \"from_iban\", iban not found");
        }
        BankAccount fromBankAccount = fromBankAccountOptional.get();

        Optional<BankAccount> toBankAccountOptional = bankAccountRepository.findByIban(body.get("from_iban").getAsString());
        if (toBankAccountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to set attribute \"to_iban\", iban not found");
        }
        BankAccount toBankAccount = fromBankAccountOptional.get();
        Double amount = body.get("amount").getAsDouble();
        LocalDateTime timestamp = LocalDateTime.parse(body.get("timestamp").getAsString());

        // Set and save transaction
        transaction.setFromBankAccount(fromBankAccount);
        transaction.setToBankAccount(toBankAccount);
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);
        transactionRepository.save(transaction);

        return ResponseEntity.status(HttpStatus.OK).body("Transaction successfully updated");
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
        bankAccountRepository.save(bankAccount);

        // Log deletion
        log.info("Transactions from user \"{}\" were deleted", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions deleted");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }


}
