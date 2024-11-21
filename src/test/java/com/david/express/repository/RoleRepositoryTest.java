package com.david.express.repository;

import com.david.express.entity.Role;
import com.david.express.entity.RoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void itShouldFindRoleByName() {
        // Given
        Role savedRole1 = roleRepository.save(new Role(1, RoleEnum.ROLE_READER));
        Role savedRole2 = roleRepository.save(new Role(2, RoleEnum.ROLE_WRITER));
        Role savedRole3 = roleRepository.save(new Role(3, RoleEnum.ROLE_ADMIN));
        // When
        Optional<Role> optionalRole = roleRepository.findByName(savedRole2.getName());
        // Then
        assertThat(optionalRole).isPresent();
        assertEquals(optionalRole.get().getName(), savedRole2.getName());
    }
}
