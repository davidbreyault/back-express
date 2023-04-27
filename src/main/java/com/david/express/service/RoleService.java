package com.david.express.service;

import com.david.express.entity.Role;

import java.util.Set;

public interface RoleService {

    Set<Role> rolesAssignmentByRegistration();
    Set<Role> rolesAssignmentByAdmin(Set<String> strRoles);
    boolean isLoggedUserHasAdminRole();
}
