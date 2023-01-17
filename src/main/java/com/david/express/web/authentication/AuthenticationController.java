package com.david.express.web.authentication;

import com.david.express.model.User;
import com.david.express.repository.RoleRepository;
import com.david.express.repository.UserRepository;
import com.david.express.security.jwt.JwtUtils;
import com.david.express.security.service.UserDetailsImpl;
import com.david.express.service.RoleService;
import com.david.express.validation.ErrorResponseBuilder;
import com.david.express.validation.dto.ErrorResponseDTO;
import com.david.express.validation.dto.SuccessResponseDTO;
import com.david.express.web.authentication.dto.request.AuthenticationRequestDTO;
import com.david.express.web.authentication.dto.request.RegistrationRequestDTO;
import com.david.express.web.authentication.dto.response.AuthenticationJwtResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequestDTO authRequest, HttpServletRequest request) {
        //System.out.println(request.getHeader("Authorization"));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new AuthenticationJwtResponseDTO(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        // Vérification disponibilité du nom d'utilisateur
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            ErrorResponseDTO errors = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Username is already in use !"
            );
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponseBuilder.build(errors));
        }
        // Vérification disponibilité de l'adresse email
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            ErrorResponseDTO errors = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Email is already in use !"
            );
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponseBuilder.build((errors)));
        }
        // Création du compte utilisateur
        User user = User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roleService.rolesAssignmentByRegistration())
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponseDTO("User registered successfully!"));
    }
}
