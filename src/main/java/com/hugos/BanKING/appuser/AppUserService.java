package com.hugos.BanKING.appuser;

import com.google.gson.JsonObject;
import com.hugos.BanKING.bankaccount.BankAccount;
import com.hugos.BanKING.bankaccount.BankAccountService;
import com.hugos.BanKING.jwt.JwtService;
import com.hugos.BanKING.role.Role;
import com.hugos.BanKING.util.EmailValidator;
import com.hugos.BanKING.util.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final BankAccountService bankAccountService;
    private final RequestService requestService;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<?> register(HttpServletRequest request) {

        // Prep response entity
        HttpStatus status = HttpStatus.OK;
        String err = null;

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        if (email.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            err = "Email is missing";
        }
        else if (!emailValidator.validate(email)) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            err = "Email is invalid";
        }
        else if (appUserRepository.findByEmail(email).isPresent()) {
            status = HttpStatus.CONFLICT;
            err = "Email already taken";
        }
        else if (password==null || password.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            err = "Password is missing";
        }
        else if(password.length() < 7) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            err = "Password is too short";
        }
        if (status!= HttpStatus.OK) {
            return ResponseEntity.status(status).body(String.format("Error: %s", err));
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
            iban = BankAccount.IBAN_PREFIX + UUID.randomUUID().toString().substring(0,8);
        } while (bankAccountService.findByIban(iban).isPresent());

        // Create and save bank account for new app user
        BankAccount bankAccount = new BankAccount(
                null,
                iban,
                appUser,
                0.0
        );
        bankAccountService.save(bankAccount);

        // Return 200
        return ResponseEntity.ok().body("Success: User registered");
    }
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        // TODO: Verify and return json with Access and Refresh token
        return null;
    }
}
