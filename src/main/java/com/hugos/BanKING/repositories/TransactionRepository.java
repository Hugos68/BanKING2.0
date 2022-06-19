package com.hugos.BanKING.repositories;

import com.hugos.BanKING.entities.BankAccount;
import com.hugos.BanKING.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    void deleteAllByFromBankAccount(BankAccount bankAccount);

    void deleteAllByToBankAccount(BankAccount bankAccount);
}
