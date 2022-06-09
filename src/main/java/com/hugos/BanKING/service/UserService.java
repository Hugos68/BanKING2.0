package com.hugos.BanKING.service;

import com.hugos.BanKING.domain.BankAccount;
import com.hugos.BanKING.domain.Role;
import com.hugos.BanKING.domain.User;
import com.hugos.BanKING.repository.UserRepo;
import com.hugos.BanKING.repository.RoleRepo;
import com.hugos.BanKING.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;



@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements  UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private final BankAccountService bankAccountService;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user ==null) {
            log.error("Email {} not found", email);
            throw new UsernameNotFoundException("Email not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }

    public ResponseEntity<?> registerUser(String name, String email, String password) {
        if (name.equals("")) {
            log.error("Name cannot be null");
            return ResponseEntity.badRequest().body("Name cannot be null");
        }

        if (!emailValidator.validate(email)) {
            log.error("{} is not a valid email", email);
            return ResponseEntity.badRequest().body(String.format("%s is not a valid email", email));
        }

        if (userRepo.findByEmail(email)!=null) {
            log.error("Email {} already taken", email);
            return ResponseEntity.badRequest().body(String.format("Email %s already taken", email));
        }
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleRepo.findByName("ROLE_USER"));
        User user = new User (
            null,
            name,
            email,
            password,
            false,
            roles
        );
        saveUser(user);

        // Create unique IBAN
        String IBAN;
        do {
            IBAN = BankAccount.IBAN_PREFIX + " " + UUID.randomUUID().toString().substring(0,8);
        } while (bankAccountService.getBankAccount(IBAN)!=null);

        // Create bank account for created user
        BankAccount bankAccount = new BankAccount(
            null,
            IBAN,
            user,
            0.0,
            LocalDateTime.now()
        );
        bankAccountService.saveBankAccount(bankAccount);
        log.info("Registration success:");
        log.info("Name: {}", name);
        log.info("Email: {}", email);
        log.info("Password: {}", password);
        return ResponseEntity.ok().build();
    }

    public User saveUser(User user) {
        log.info("Saving new user {}", user.getEmail());
        // Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public Role saveRole(Role role) {
        log.info("Saving new role {}", role.getName());
        return roleRepo.save(role);
    }

    public void addRoleToUser(String email, String roleName) {
        log.info("Adding role {} to user {}", roleName, email);
        Role role = roleRepo.findByName(roleName);
        User user = userRepo.findByEmail(email);
        user.getRoles().add(role);
    }

    public User getUser(String email) {
        log.info("Fetching user {}", email);
        return userRepo.findByEmail(email);
    }

    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }
}
