package com.hugos.BanKING.services;

import com.google.gson.JsonObject;
import com.hugos.BanKING.models.AppUser;
import com.hugos.BanKING.models.BankAccount;
import com.hugos.BanKING.models.DecodedAccessToken;
import com.hugos.BanKING.repositories.BankAccountRepository;
import com.hugos.BanKING.repositories.AppUserRepository;
import com.hugos.BanKING.enums.Role;
import com.hugos.BanKING.util.EmailValidator;
import com.hugos.BanKING.util.RequestUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final BankAccountRepository bankAccountRepository;
    private final RequestUtility requestUtility;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public ResponseEntity<?> register(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Data validation
        if (email.equals("")) {
            jsonObject.addProperty("message", "Email is missing");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }
        else if (!EmailValidator.validate(email)) {
            jsonObject.addProperty("message", "Email is invalid");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }
        else if (appUserRepository.findByEmail(email).isPresent()) {

            jsonObject.addProperty("message", "Email already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonObject.toString());
        }
        else if (password==null || password.equals("")) {

            jsonObject.addProperty("message", "Password is missing");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }
        else if (password.length() < 7) {
            jsonObject.addProperty("message", "Password is too short");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
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
            iban = BankAccount.IBAN_PREFIX + " "+UUID.randomUUID().toString().substring(0,8);
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
        log.info("User registered: [email: \"{}\", password: \"{}\"]", email, password);

        // Create json response body
        jsonObject.addProperty("message", "User registered");

        // Return response
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }
    public ResponseEntity<?> authenticate(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestUtility.getJsonFromRequest(request);
        String email = body.get("email").getAsString().toLowerCase();
        String password = body.get("password").getAsString();

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Data validation
        if (email.equals("")) {
            jsonObject.addProperty("message", "Email is missing");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }
        else if (!EmailValidator.validate(email) || appUserRepository.findByEmail(email).isEmpty()) {
            jsonObject.addProperty("message", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject.toString());
        }
        else if (password==null || password.equals("")) {
            jsonObject.addProperty("message", "Password is missing");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }
        else if (!bCryptPasswordEncoder.matches(password, appUserRepository.findByEmail(email).get().getPassword())) {
            jsonObject.addProperty("message", "Password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
        }

        // Get jwt pair
        Map<String,String> tokenPair = jwtService.createAccessRefreshTokenPair(appUserRepository.findByEmail(email).get());

        // Log authentication
        log.info("User authenticated: [email: \"{}\", password: \"{}\"]", email, password);

        // Create json response body
        jsonObject.addProperty("access_token", tokenPair.get("access_token"));
        jsonObject.addProperty("refresh_token", tokenPair.get("refresh_token"));
        jsonObject.addProperty("message", "User authenticated");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> getAccount(HttpServletRequest request) {

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        AppUser appUser = appUserRepository.findByEmail(
            decodedAccessToken.subject()
        ).get();

        BankAccount bankAccount = bankAccountRepository.findByAppUser(appUser).get();

        // Create json response body
        JsonObject jsonBank = new JsonObject();
        jsonBank.addProperty("id", bankAccount.getId());
        jsonBank.addProperty("iban", bankAccount.getIban());
        jsonBank.addProperty("balance", bankAccount.getBalance());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", appUser.getId());
        jsonObject.addProperty("email", appUser.getEmail());
        jsonObject.add("bank_account", jsonBank);
        jsonObject.addProperty("message", "Account fetched");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {

        // Retrieve and decode access token
        String accessToken;
        try {
            accessToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        } catch (Exception exception) {
            accessToken = null;
        }

        DecodedAccessToken decodedAccessToken = jwtService.decodeAccessToken(accessToken);

        AppUser appUser = appUserRepository.findByEmail(
            decodedAccessToken.subject()
        ).get();

        bankAccountRepository.delete(
            bankAccountRepository.findByAppUser(appUser).get()
        );
        appUserRepository.delete(appUser);

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Account deleted");

        // Return response
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }
}