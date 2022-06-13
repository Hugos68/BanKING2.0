package com.hugos.BanKING.authenticate;

import com.hugos.BanKING.appuser.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserService appUserService;

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        return appUserService.authenticate(request);
    }
}
