package com.hugos.BanKING.appuser;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AppUserRepository implements CrudRepository<Long, AppUser> {
    @Override
    public <S extends Long> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Long> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Long> findById(AppUser appUser) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(AppUser appUser) {
        return false;
    }

    @Override
    public Iterable<Long> findAll() {
        return null;
    }

    @Override
    public Iterable<Long> findAllById(Iterable<AppUser> appUsers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(AppUser appUser) {

    }

    @Override
    public void delete(Long entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends AppUser> appUsers) {

    }

    @Override
    public void deleteAll(Iterable<? extends Long> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
