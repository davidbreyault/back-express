package com.david.express.web.authentication;

import com.david.express.model.Role;
import com.david.express.model.RoleEnum;
import com.david.express.model.User;
import com.david.express.repository.RoleRepository;
import com.david.express.repository.UserRepository;
import com.david.express.security.jwt.JwtUtils;
import com.david.express.security.service.UserDetailsImpl;
import com.david.express.web.authentication.dto.request.AuthenticationRequestDTO;
import com.david.express.web.authentication.dto.request.RegistrationRequestDTO;
import com.david.express.web.authentication.dto.response.AuthenticationSuccessResponseDTO;
import com.david.express.web.authentication.dto.response.RegistrationErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
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
    private JwtUtils jwtUtils;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequestDTO authRequest) {
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
        return ResponseEntity.ok(new AuthenticationSuccessResponseDTO(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        // Vérification disponibilité du nom d'utilisateur
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new RegistrationErrorResponseDTO("Error: Username is already taken !"));
        }
        // Vérification disponibilité de l'adresse email
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new RegistrationErrorResponseDTO("Error: Email is already in use !"));
        }
        // Création du compte utilisateur
        User user = User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .build();

        // Attribution des rôles
        Set<String> strRoles = registrationRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_READER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "writer":
                        Role writerRole = roleRepository.findByName(RoleEnum.ROLE_WRITER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(writerRole);
                        break;
                    default:
                        Role readerRole = roleRepository.findByName(RoleEnum.ROLE_READER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(readerRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new RegistrationErrorResponseDTO("User registered successfully!"));
    }
}
