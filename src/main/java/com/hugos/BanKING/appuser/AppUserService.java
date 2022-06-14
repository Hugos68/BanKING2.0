package com.hugos.BanKING.appuser;

import com.google.gson.JsonObject;
import com.hugos.BanKING.bankaccount.BankAccount;
import com.hugos.BanKING.bankaccount.BankAccountService;
import com.hugos.BanKING.jwt.JwtService;
import com.hugos.BanKING.jwt.JwtServiceHandler;
import com.hugos.BanKING.jwt.tokens.DecodedRefreshToken;
import com.hugos.BanKING.jwt.tokens.TokenType;
import com.hugos.BanKING.role.Role;
import com.hugos.BanKING.util.EmailValidator;
import com.hugos.BanKING.util.RequestService;
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
    private final BankAccountService bankAccountService;
    private final RequestService requestService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final JwtServiceHandler jwtServiceHandler;

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public ResponseEntity<?> register(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
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

        // Log registration
        log.info("User registered: [email: \"{}\", password: \"{}\"]", email, password);

        // Respond to request
        jsonObject.addProperty("message", "User registered");
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }
    public ResponseEntity<?> authenticate(HttpServletRequest request) {

        // Get data from request
        JsonObject body = requestService.getJsonFromRequest(request);
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

        // Respond to request
        jsonObject.addProperty("access_token", tokenPair.get("access_token"));
        jsonObject.addProperty("refresh_token", tokenPair.get("refresh_token"));
        jsonObject.addProperty("message", "User authenticated");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }


    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {

        // Get authorization token
        String refreshToken = request.getHeader(AUTHORIZATION).substring("Bearer ".length());

        // Get decoded token from request
        DecodedRefreshToken decodedRefreshToken = jwtService.decodeRefreshToken(refreshToken);

        // Create response object
        JsonObject jsonObject = new JsonObject();

        // Token validation
        if (decodedRefreshToken==null ||
            decodedRefreshToken.isExpired() ||
            appUserRepository.findByEmail(decodedRefreshToken.subject()).isEmpty() ) {
            jsonObject.addProperty("message", "Refresh token is invalid");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(jsonObject.toString());
        }

        // Get token pair (Note: refresh is not used to force authentication after 1 week)
        Map<String,String> tokenPair = jwtService.createAccessRefreshTokenPair(
                appUserRepository.findByEmail(decodedRefreshToken.subject()).get()
        );

        // Respond to request
        jsonObject.addProperty("access_token", tokenPair.get("access_token"));
        jsonObject.addProperty("message", "Refresh token was validated");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> getEmail(HttpServletRequest request) {

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", jwtServiceHandler.getBearerEmail(request, TokenType.ACCESS));
        jsonObject.addProperty("message", "Email fetched");

        // Respond to request
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity<?> getBalance(HttpServletRequest request) {

        AppUser appUser = appUserRepository.findByEmail(jwtServiceHandler.getBearerEmail(request, TokenType.ACCESS)).get();

        BankAccount bankAccount = bankAccountService.findByAppUser(appUser).get();

        // Create json response body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("balance", bankAccount.getBalance());
        jsonObject.addProperty("message", "Balance fetched");

        // Respond to request
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }
}