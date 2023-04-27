package com.david.express.repository;

import com.david.express.entity.Role;
import com.david.express.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum roleEnum);
}
