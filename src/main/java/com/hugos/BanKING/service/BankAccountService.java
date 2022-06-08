package com.hugos.BanKING.service;

import com.hugos.BanKING.domain.BankAccount;
import com.hugos.BanKING.domain.User;
import com.hugos.BanKING.repository.BankAccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final BankAccountRepo bankAccountRepo;

    public BankAccount saveBankAccount(BankAccount bankAccount) {
        log.info("Saving new bank account for user {}", bankAccount.getUser().getEmail());
        return bankAccountRepo.save(bankAccount);
    }
    public BankAccount getBankAccount(User user) {
        return bankAccountRepo.findByUser(user);
    }
    public BankAccount getBankAccount(String IBAN) {
        return bankAccountRepo.findByIBAN(IBAN);
    }

}
