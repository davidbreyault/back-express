package com.david.express.web;

import com.david.express.security.service.SecurityService;
import com.david.express.validation.dto.SuccessResponseDto;
import com.david.express.model.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private SecurityService securityService;

    @PostMapping("/authenticate")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        // Appel à SecurityService pour authentifier l'utilisateur
        String jwt = securityService.authenticateUser(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", jwt);
        return ResponseEntity.ok().headers(responseHeaders).body(new SuccessResponseDto("Authenticated !"));
    }

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationRequest) {
        // Appel au service de sécurité pour enregistrer l'utilisateur
        securityService.registerUser(registrationRequest);
        return new ResponseEntity<>(new SuccessResponseDto("User registered successfully!"), HttpStatus.CREATED);
    }
}
