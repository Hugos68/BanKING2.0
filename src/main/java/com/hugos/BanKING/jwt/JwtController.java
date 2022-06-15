package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/api/token")
@AllArgsConstructor
public class JwtController {

    private final AppUserService appUserService;

    @CrossOrigin
    @GetMapping(path = "/pair")
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        log.info("Endpoint: \"api/authentication\" was called");
        return appUserService.authenticate(request);
    }

    @CrossOrigin
    @GetMapping(path = "/access")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        log.info("Endpoint: \"api/refresh-token\" was called");
        return appUserService.refreshAccessToken(request);
    }


}
