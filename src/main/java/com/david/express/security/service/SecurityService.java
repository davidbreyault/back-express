package com.david.express.security.service;

import com.david.express.entity.Role;
import com.david.express.entity.RoleEnum;
import com.david.express.entity.User;
import com.david.express.exception.ResourceAlreadyExistsException;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.repository.RoleRepository;
import com.david.express.security.jwt.JwtUtils;
import com.david.express.service.UserService;
import com.david.express.model.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class SecurityService {

    private final String anonymousUser = "anonymousUser";

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;


    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getName().equals(anonymousUser)) {
            return authentication.getName();
        }
        return null;
    }

    public User getAuthenticatedUser() throws ResourceNotFoundException {
        String username = getAuthenticatedUsername();
        return userService.findUserByUsername(username);
    }

    public boolean isUserAuthenticated() {
        return Objects.nonNull(getAuthenticatedUsername()) && !getAuthenticatedUsername().equals(anonymousUser);
    }

    public String authenticateUser(HttpServletRequest request) {
        // Récupération du Basic Auth dans la requête
        final String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            throw new InsufficientAuthenticationException("Authentication failed");
        }
        // Décodage de la chaîne de caractères, puis récupération du username et du password
        String encodedCredentials = authorization.substring(6);
        byte[] decodedValue = Base64.getDecoder().decode(encodedCredentials);
        String decodedCredentials = new String(decodedValue);
        final String[] values = decodedCredentials.split(":", 2);
        String username = values[0];
        String password = values[1];
        // Authentification de l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username,password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Création du JWT
        return jwtUtils.generateJwtToken(authentication);
    }

    public void registerUser(RegistrationDto registrationRequest) {
        // Vérification disponibilité du nom d'utilisateur
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username " + registrationRequest.getUsername() + " is already in use !");
        }
        // Vérification disponibilité de l'adresse email
        if (userService.existsByEmail(registrationRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email " + registrationRequest.getEmail() + " is already in use !");
        }
        // Création du compte utilisateur
        userService.saveUser(
            User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(assignRolesOnRegistration())
                .build()
        );
    }

    private Set<Role> assignRolesOnRegistration() {
        // Par défaut, l'inscription donne accès au ROLE_WRITER
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleEnum.ROLE_READER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Role writerRole = roleRepository.findByName(RoleEnum.ROLE_WRITER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        roles.add(writerRole);
        return roles;
    }

    public boolean isLoggedUserHasAdminRole() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
