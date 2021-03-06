package com.hugos.BanKING2.repositories;

import com.hugos.BanKING2.entities.BankAccount;
import com.hugos.BanKING2.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<List<Transaction>> findAllByFromBankAccount(BankAccount bankAccount);
    Optional<List<Transaction>>  findAllByToBankAccount(BankAccount bankAccount);
    void deleteAllByFromBankAccount(BankAccount bankAccount);
    void deleteAllByToBankAccount(BankAccount bankAccount);
}