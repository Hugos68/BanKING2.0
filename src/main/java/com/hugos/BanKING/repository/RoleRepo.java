package com.hugos.BanKING.repository;

import com.hugos.BanKING.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
}
