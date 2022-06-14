package com.hugos.BanKING.authenticate;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticate;

    @CrossOrigin
    @PostMapping
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: \"api/authentication\" was called");
        return authenticate.authenticate(request);
    }
}
