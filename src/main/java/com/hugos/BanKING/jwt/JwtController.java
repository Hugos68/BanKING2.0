package com.hugos.BanKING.jwt;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/token/pair-refresh")
@AllArgsConstructor
public class JwtController {

    private final AppUserService appUserService;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return appUserService.refreshTokenPair(request);
    }
}
