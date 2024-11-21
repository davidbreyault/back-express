package com.david.express.repository;

import com.david.express.entity.RoleEnum;
import com.david.express.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    private User user1;
    private User user2;
    private User user3;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        user1 = User.builder()
            .id(1L)
            .username("John")
            .email("johndoe@gmail.com")
            .password("password")
            .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
            .build();
        user2 = User.builder()
            .id(2L)
            .username("Jeannot")
            .email("jeanpetit@gmail.com")
            .password("password")
            .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
            .build();
        user3 = User.builder()
            .id(3L)
            .username("Jack")
            .email("jacksparrow@gmail.com")
            .password("password")
            .roles(new HashSet<>(RoleEnum.ROLE_READER.ordinal(), RoleEnum.ROLE_WRITER.ordinal()))
            .build();
    }

    @Test
    public void itShouldFindAllUsers() {
        // Given
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        // When
        List<User> users = userRepository.findAll();
        // Then
        assertEquals(3, users.size());
        assertThat(users).hasSize(3);
    }

    @Test
    public void itShouldFindUserById() {
        // Given
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        // When
        Optional<User> optionalUser = userRepository.findById(savedUser1.getId());
        // Then
        assertTrue(optionalUser.isPresent());
        assertEquals(savedUser1, optionalUser.get());
        assertNotEquals(savedUser2, optionalUser.get());
        assertNotEquals(savedUser3, optionalUser.get());
    }

    @Test
    public void itShouldSaveUser() {
        // When
        User savedUser1 = userRepository.save(user1);
        // Then
        assertThat(savedUser1).isNotNull();
        assertThat(savedUser1.getId()).isGreaterThan(0);
        assertEquals(user1.getUsername(), savedUser1.getUsername());
    }

    @Test
    public void itShouldFindUserByUsername() {
        // Given
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        // When
        String username = user2.getUsername();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Then
        assertTrue(optionalUser.isPresent());
        assertEquals(savedUser2, optionalUser.get());
        assertNotEquals(savedUser1, optionalUser.get());
        assertNotEquals(savedUser3, optionalUser.get());
    }

    @Test
    public void itShouldAssertTrueIfUserExistsProvingItsUsername() {
        // Given
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        // When
        String username = user2.getUsername();
        boolean exists = userRepository.existsByUsername(username);
        // Then
        assertTrue(exists);
    }

    @Test
    public void itShouldAssertFalseIfUserExistsProvingItsUsername() {
        // Given
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        // When
        String username = "victor_hugo";
        boolean exists = userRepository.existsByUsername(username);
        // Then
        assertFalse(exists);
    }

    @Test
    public void itShouldAssertTrueIfUserExistsProvingItsEmail() {
        // Given
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        // When
        String email = user3.getEmail();
        boolean exists = userRepository.existsByEmail(email);
        // Then
        assertTrue(exists);
    }

    @Test
    public void itShouldAssertFalseIfUserExistsProvingItsEmail() {
        // Given
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        // When
        String email = "victorhugo@litterature.fr";
        boolean exists = userRepository.existsByEmail(email);
        // Then
        assertFalse(exists);
    }

    @Test
    public void itShouldDeleteUser() {
        // Given
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        // When
        userRepository.delete(savedUser1);
        List<User> users = userRepository.findAll();
        Optional<User> optionalUser = userRepository.findById(user1.getId());
        // Then
        assertThat(optionalUser).isEmpty();
        assertThat(users).hasSize(2).contains(savedUser2, savedUser3);
    }
}
