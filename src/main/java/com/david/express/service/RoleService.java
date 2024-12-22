package com.david.express.service;

import com.david.express.entity.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> assignRolesForUserByAdmin(Set<String> stringRoles);
}
