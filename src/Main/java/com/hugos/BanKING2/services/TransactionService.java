package com.hugos.BanKING2.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING2.entities.BankAccount;
import com.hugos.BanKING2.entities.Transaction;
import com.hugos.BanKING2.enums.TransactionType;
import com.hugos.BanKING2.repositories.BankAccountRepository;
import com.hugos.BanKING2.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {

    private final RequestService requestService;
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

    public ResponseEntity<?> getTransactions(String iban, int limit, String sortBy) {

        BankAccount bankAccount = bankAccountRepository.findByIban(iban).get();

        // Get all transactions related to this bank account
        JsonObject transactionsObject = new JsonObject();
        Optional<List<Transaction>> optionalFromList = transactionRepository.findAllByFromBankAccount(bankAccount);
        Optional<List<Transaction>> optionalToList = transactionRepository.findAllByToBankAccount(bankAccount);

        List<Transaction> transactionList = new ArrayList<>();
        optionalFromList.ifPresent(transactionList::addAll);
        optionalToList.ifPresent(transactionList::addAll);

        // Limit list size when limit was specified
        if (limit!=0 && limit <= transactionList.size()) {
            transactionList = transactionList.subList(0, limit);
        }

        // Sort list, default is id
        if (sortBy==null || sortBy.equals("timestamp")) {
            transactionList.sort(Comparator.comparing(Transaction::getTimestamp));
        }
        else if (sortBy.equals("type")) {
            transactionList.sort(Comparator.comparing(Transaction::getType));
        }
        else if (sortBy.equals("amount")) {
            transactionList.sort(Comparator.comparing(Transaction::getAmount));
        }

        // Format list to a JSON Object
        for (Transaction transaction : transactionList) {
            JsonObject transactionObject = new JsonObject();

            // Mark any null bank accounts as 'Unknown source'
            String ibanFrom;
            if (transaction.getFromBankAccount()==null) ibanFrom="unknown source";
            else ibanFrom = transaction.getFromBankAccount().getIban();
            String ibanTo;
            if (transaction.getToBankAccount()==null) ibanTo = "unknown source";
            else ibanTo = transaction.getToBankAccount().getIban();

            // Format all transaction information
            transactionObject.addProperty("id", transaction.getId());
            transactionObject.addProperty("type", transaction.getType().name());
            transactionObject.addProperty("iban_from", ibanFrom);
            transactionObject.addProperty("iban_to", ibanTo);
            transactionObject.addProperty("amount", transaction.getAmount());
            transactionObject.addProperty("date_time", transaction.getTimestamp().toString());
            transactionsObject.add("transaction_"+transaction.getId(), transactionObject);
        }

        // Log fetch
        log.info("Transactions from bank account: \"{}\" were fetched", iban);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions retrieved");
        jsonObject.add("transactions", transactionsObject);
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> updateTransaction(HttpServletRequest request, Long id) {

        // Check if transaction exists
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        Transaction transaction = optionalTransaction.get();

        // Get body from request
        JsonObject body = requestService.getJsonFromRequest(request);
        Double amount = body.get("amount").getAsDouble();

        // Set and save transaction
        transaction.setAmount(amount);
        transactionRepository.save(transaction);
        return ResponseEntity.status(HttpStatus.OK).body("Transaction successfully updated");
    }

    public ResponseEntity<?> deleteTransactions(String iban) {

        BankAccount bankAccount = bankAccountRepository.findByIban(iban).get();

        // Clear all transactions from bank account
        transactionRepository.deleteAllByFromBankAccount(bankAccount);
        transactionRepository.deleteAllByToBankAccount(bankAccount);
        bankAccountRepository.save(bankAccount);

        // Log deletion
        log.info("Transactions from bank account: \"{}\" were deleted", iban);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Transactions deleted");

        // Return response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }
}
