package com.hugos.BanKING.repository;

import com.hugos.BanKING.domain.BankAccount;
import com.hugos.BanKING.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {

    BankAccount findByUser(User user);
    BankAccount findByIBAN(String IBAN);
}
