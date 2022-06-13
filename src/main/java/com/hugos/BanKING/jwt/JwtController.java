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
@RequestMapping(path = "/api/token/access/refresh")
@AllArgsConstructor
public class JwtController {

    private final AppUserService appUserService;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        log.info("Endpoint: \"api/token/pair-refresh\" was called");
        return appUserService.refreshAccessToken(request);
    }
}
