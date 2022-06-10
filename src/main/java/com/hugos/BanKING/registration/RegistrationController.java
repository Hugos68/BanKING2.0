package com.hugos.BanKING.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class RegistrationController {

    @GetMapping("/registration")
    public ResponseEntity<?> register(HttpServletRequest request) {
        return null;
    }
}
