package com.david.express.service.impl;

import com.david.express.entity.Role;
import com.david.express.entity.RoleEnum;
import com.david.express.repository.RoleRepository;
import com.david.express.repository.UserRepository;
import com.david.express.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public Set<Role> rolesAssignmentByRegistration() {
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

    public Set<Role> rolesAssignmentByAdmin(Set<String> strRoles) {
        // Attribution des rôles choisi par un utilisateur ayant le rôle d'administrateur
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
        return roles;
    }

    @Override
    public boolean isLoggedUserHasAdminRole() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
