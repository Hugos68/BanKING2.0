package com.hugos.BanKING.registration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hugos.BanKING.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final AppUserService appUserService;

    @PostMapping
    public ResponseEntity<?> register(HttpServletRequest request) {
        return appUserService.register(request);
    }
}
