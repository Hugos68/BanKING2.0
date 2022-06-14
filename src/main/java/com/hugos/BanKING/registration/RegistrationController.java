package com.hugos.BanKING.registration;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final AppUserService appUserService;

    @CrossOrigin
    @PostMapping
    public ResponseEntity<?> register(HttpServletRequest request) {
        log.info("Endpoint: \"api/registration\" was called");
        return appUserService.register(request);
    }
}
