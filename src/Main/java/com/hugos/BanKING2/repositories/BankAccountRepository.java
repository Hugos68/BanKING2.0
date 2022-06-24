package com.hugos.BanKING2.repositories;

import com.hugos.BanKING2.entities.AppUser;
import com.hugos.BanKING2.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByIban(String iban);
    Optional<BankAccount> findByAppUser(AppUser appUser);
}