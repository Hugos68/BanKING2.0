package com.hugos.BanKING.registration;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final AppUserService appUserService;

    @PostMapping
    @CrossOrigin(origins = "http://localhost:63342")
    public ResponseEntity<?> register(HttpServletRequest request) {
        return appUserService.register(request);
    }
}
