package com.hugos.BanKING.repositories;

import com.hugos.BanKING.entities.BankAccount;
import com.hugos.BanKING.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<List<Transaction>> findAllByFromBankAccount(BankAccount bankAccount);
    Optional<List<Transaction>>  findAllByToBankAccount(BankAccount bankAccount);
    void deleteAllByFromBankAccount(BankAccount bankAccount);
    void deleteAllByToBankAccount(BankAccount bankAccount);
}
