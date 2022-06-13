package com.hugos.BanKING.authenticate;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AppUserService appUserService;

    @PostMapping
    @CrossOrigin
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        return appUserService.authenticate(request);
    }
}
