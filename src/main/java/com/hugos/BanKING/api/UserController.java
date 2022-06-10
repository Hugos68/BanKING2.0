package com.hugos.BanKING.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hugos.BanKING.domain.User;
import com.hugos.BanKING.domain.Role;
import com.hugos.BanKING.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request.getName(), request.getEmail().toLowerCase(), request.getPassword());
    }

    @GetMapping("/users")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/user/save")
    @CrossOrigin(origins = "*")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserRequest request) {
        userService.addRoleToUser(request.getEmail(), request.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String email = decodedJWT.getSubject();
                User user = userService.getUser(email);

                String accessToken = JWT.create()
                    .withSubject(user.getEmail())
                    // Access token is set to expire 5 minutes after creation
                    .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", user.getRoles().stream().map(
                        Role::getName).collect(Collectors.toList()))
                    .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                ResponseEntity.ok().body(tokens);
            } catch (Exception exception) {
                log.error("Error: Invalid refresh token");
                return ResponseEntity.status(BAD_REQUEST).body("Error: Invalid refresh token");
            }
        }
        log.error("Error: Missing refresh token");
        return ResponseEntity.status(BAD_REQUEST).body("Error: Missing refresh token");
    }
}

@Data
class RoleToUserRequest {
    private String email;
    private String roleName;
}

@Data
class RegisterRequest {
    private String name;
    private String email;
    private String password;
}
