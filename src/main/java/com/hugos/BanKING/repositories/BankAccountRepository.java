package com.hugos.BanKING.repositories;

import com.hugos.BanKING.models.AppUser;
import com.hugos.BanKING.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByIban(String iban);

    Optional<BankAccount> findByAppUser(AppUser appUser);
}
