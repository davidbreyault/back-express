package com.david.express.service.impl;

import com.david.express.entity.Role;
import com.david.express.entity.RoleEnum;
import com.david.express.repository.RoleRepository;
import com.david.express.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Set<Role> assignRolesForUserByAdmin(Set<String> stringRoles) {
        // Attribution des rôles choisi par un utilisateur ayant le rôle d'administrateur
        Set<Role> roles = new HashSet<>();
        if (stringRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_READER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            stringRoles.forEach(role -> {
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
}
