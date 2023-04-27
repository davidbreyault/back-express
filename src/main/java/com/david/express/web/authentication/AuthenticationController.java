package com.david.express.web.authentication;

import com.david.express.exception.ResourceAlreadyExistsException;
import com.david.express.entity.User;
import com.david.express.security.jwt.JwtUtils;
import com.david.express.service.impl.RoleServiceImpl;
import com.david.express.service.UserService;
import com.david.express.validation.dto.SuccessResponseDTO;
import com.david.express.web.authentication.dto.request.RegistrationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/authenticate")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        // Récupération des identifiants dans la requête
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic ")) {
            String encodedCredentials = request.getHeader("Authorization").substring(6);
            byte[] valueDecoded = Base64.getDecoder().decode(encodedCredentials);
            String decodedCredentials = new String(valueDecoded);
            final String[] values = decodedCredentials.split(":", 2);
            String username = values[0];
            String password = values[1];
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username,password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", jwt);
            return ResponseEntity.ok().headers(responseHeaders).body(new SuccessResponseDTO("Authenticated !"));
        } else {
            throw new AuthenticationException("Authentication failed") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }
    }

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<SuccessResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        // Vérification disponibilité du nom d'utilisateur
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username " + registrationRequest.getUsername() + " is already in use !");
        }
        // Vérification disponibilité de l'adresse email
        if (userService.existsByEmail(registrationRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email " + registrationRequest.getEmail() + " is already in use !");
        }
        // Création du compte utilisateur
        User user = userService.saveUser(
                User.builder()
                        .username(registrationRequest.getUsername())
                        .email(registrationRequest.getEmail())
                        .password(passwordEncoder.encode(registrationRequest.getPassword()))
                        .roles(roleService.rolesAssignmentByRegistration())
                        .build()
        );
        return new ResponseEntity<>(new SuccessResponseDTO("User registered successfully!"), HttpStatus.CREATED);
    }
}
