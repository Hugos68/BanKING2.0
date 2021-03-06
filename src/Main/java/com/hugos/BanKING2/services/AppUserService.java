package com.hugos.BanKING2.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING2.entities.AppUser;
import com.hugos.BanKING2.entities.BankAccount;
import com.hugos.BanKING2.repositories.BankAccountRepository;
import com.hugos.BanKING2.repositories.AppUserRepository;
import com.hugos.BanKING2.enums.Role;
import com.hugos.BanKING2.repositories.TransactionRepository;
import com.hugos.BanKING2.util.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {

    private final TokenService tokenService;
    private final RequestService requestService;
    private final AppUserRepository appUserRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ResponseEntity<?> createAppUser(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Data validation
        if (email.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email is missing");
        }
        if (!EmailValidator.validate(email)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email is invalid");
        }
        if (appUserRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }
        if (password == null || password.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Password is missing");
        }
        if (password.length() < 7) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Password is too short");
        }

        // TODO: Create and save salt for every user (Optional)

        // Create and save new app user
        AppUser appUser = new AppUser(null,
            email,
            bCryptPasswordEncoder.encode(password),
            Role.USER);
        appUserRepository.save(appUser);

        // Create unique iban for app user
        String iban;
        do {
            iban = BankAccount.IBAN_PREFIX + " " + UUID.randomUUID().toString().substring(0, 8);
        } while (bankAccountRepository.findByIban(iban).isPresent());

        // Create and save bank account for new app user
        BankAccount bankAccount = new BankAccount(
            null,
            iban,
            appUser,
            0.0
        );
        bankAccountRepository.save(bankAccount);

        // Log registration
        log.info("User: \"{}\" was created", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "User created");
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }

    public ResponseEntity<?> getAppUser(String email) {

        AppUser appUser = getAppUserFromEmail(email);
        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // Log fetch
        log.info("User: \"{}\" was fetched", appUser.getEmail());

        // Create json response body
        JsonObject jsonBank = new JsonObject();
        jsonBank.addProperty("id", bankAccount.getId());
        jsonBank.addProperty("iban", bankAccount.getIban());
        jsonBank.addProperty("balance", bankAccount.getBalance());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", appUser.getId());
        jsonObject.addProperty("email", appUser.getEmail());
        jsonObject.add("bank_account", jsonBank);
        jsonObject.addProperty("message", "User fetched");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> updateAppUser(HttpServletRequest request, String email) {

        AppUser appUser = getAppUserFromEmail(email);

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String oldPassword = body.get("old-password").getAsString();
        String newPassword = body.get("new-password").getAsString();
        String encryptedNewPassword = bCryptPasswordEncoder.encode(newPassword);


        // Validation
        if (!bCryptPasswordEncoder.matches(oldPassword, appUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect old password");
        }
        if (newPassword.length() < 7) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "New password is too short");
        }
        if (bCryptPasswordEncoder.matches(newPassword, appUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New and old passwords are equal");
        }

        // Set and save new password
        appUser.setPassword(encryptedNewPassword);
        appUserRepository.save(appUser);

        // Create json response body    
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Password updated");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> deleteAppUser(String email) {

        AppUser appUser = getAppUserFromEmail(email);
        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // Delete all transactions that involved the to be deleted user
        transactionRepository.deleteAllByFromBankAccount(bankAccount);
        transactionRepository.deleteAllByToBankAccount(bankAccount);

        // Delete bank account from user
        bankAccountRepository.delete(bankAccount);

        // Delete user
        appUserRepository.delete(appUser);

        // Log deletion
        log.info("User: \"{}\" was deleted", appUser.getEmail());

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "User deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jsonObject.toString());
    }

    public ResponseEntity<?> authenticateAppUser(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Data validation
        if (email.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email is missing");
        } else if (!EmailValidator.validate(email) || appUserRepository.findByEmail(email).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found");
        } else if (password == null || password.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Password is missing");
        } else if (!bCryptPasswordEncoder.matches(password, appUserRepository.findByEmail(email).get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is incorrect");
        }

        // Get jwt pair
        Map<String, String> tokenPair = tokenService.createAccessRefreshTokenPair(appUserRepository.findByEmail(email).get());

        // Log authentication
        log.info("User: \"{}\" was authenticated", email);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("access_token", tokenPair.get("access_token"));
        jsonObject.addProperty("refresh_token", tokenPair.get("refresh_token"));
        jsonObject.addProperty("message", "User authenticated");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    private AppUser getAppUserFromEmail(String email) {
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return optionalAppUser.get();
    }
}