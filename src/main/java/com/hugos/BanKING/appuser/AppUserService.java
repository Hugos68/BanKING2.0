package com.hugos.BanKING.appuser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hugos.BanKING.bankaccount.BankAccount;
import com.hugos.BanKING.bankaccount.BankAccountService;
import com.hugos.BanKING.jsonwebtoken.JwtService;
import com.hugos.BanKING.role.Role;
import com.hugos.BanKING.util.EmailValidator;
import com.hugos.BanKING.util.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public ResponseEntity<?> register(HttpServletRequest request) {

        // Prep response entity
        Map<String, String> responseMap = new HashMap<>();
        HttpStatus status = null;
        String message = null;

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Data validation
        if (email.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Email is missing";
        }
        else if (!emailValidator.validate(email)) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Email is invalid";
        }
        else if (appUserRepository.findByEmail(email).isPresent()) {
            status = HttpStatus.CONFLICT;
            message = "Email already taken";
        }
        else if (password==null || password.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Password is missing";
        }
        else if(password.length() < 7) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Password is too short";
        }
        else {
            status = HttpStatus.CREATED;
            message = "User registered";
        }

        // Check if something was wrong, if so, return 400 code
        if (status != HttpStatus.CREATED) {
            responseMap.put("message", message);
            String responseBody = new Gson().toJson(responseMap);
            return ResponseEntity.status(status).body(responseBody);
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

        // Create json body
        responseMap.put("message", message);
        String responseBody = new Gson().toJson(responseMap);

        return ResponseEntity.status(status).body(responseBody);
    }
    public ResponseEntity<?> authenticate(HttpServletRequest request) {

        // Prep response entity
        Map<String, String> responseMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        String message = null;

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Data validation
        if (email.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Email is missing";
        }
        else if (appUserRepository.findByEmail(email).isEmpty()) {
            status = HttpStatus.CONFLICT;
            message = "Email not found";
        }
        else if (password==null || password.equals("")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = "Password is missing";
        }
        else if (!bCryptPasswordEncoder.matches(password, appUserRepository.findByEmail(email).get().getPassword())) {
            status = HttpStatus.BAD_REQUEST;
            message = "Password is incorrect";
        }
        else {
            status = HttpStatus.OK;
            message = "User authenticated";
        }

        // Check if something was wrong, if so, return 400 code
        if (status!= HttpStatus.OK) {
            responseMap.put("message", message);
            String responseBody = new Gson().toJson(responseMap);
            return ResponseEntity.status(status).body(responseBody);
        }

        Map<String,String> tokenPair = jwtService.createAccessRefreshTokenPair(appUserRepository.findByEmail(email).get());

        // Create json body
        responseMap.put("message", message);
        responseMap.put("access_token", tokenPair.get("access_token"));
        responseMap.put("refresh_token", tokenPair.get("refresh_token"));
        String responseBody = new Gson().toJson(responseMap);

        // Respond to request
        return ResponseEntity.status(status).body(responseBody);
    }
}
