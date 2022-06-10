package com.hugos.BanKING.bankaccount;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public class BankAccountRepository implements CrudRepository<Long, BankAccount> {

    @Override
    public <S extends Long> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Long> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Long> findById(BankAccount bankAccount) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(BankAccount bankAccount) {
        return false;
    }

    @Override
    public Iterable<Long> findAll() {
        return null;
    }

    @Override
    public Iterable<Long> findAllById(Iterable<BankAccount> bankAccounts) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(BankAccount bankAccount) {

    }


    @Override
    public void delete(Long entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends BankAccount> bankAccounts) {

    }

    @Override
    public void deleteAll(Iterable<? extends Long> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
