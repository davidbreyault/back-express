package com.david.express.service;

import com.david.express.common.Utils;
import com.david.express.entity.RoleEnum;
import com.david.express.entity.User;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.mapper.UserMapper;
import com.david.express.repository.UserRepository;
import com.david.express.model.dto.UserUpdateDto;
import com.david.express.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
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
        Page<User> usersPage = new PageImpl<>(Arrays.asList(user1, user2, user3));
        given(userRepository.findAll(any(Pageable.class))).willReturn(usersPage);
        // When
        Pageable paging = Utils.createPaging(0, 500, new String[]{"id", "desc"});
        Page<User> users = userService.findAllUsers(paging);
        // Then
        assertThat(users).isNotNull();
        assertThat(users.getContent()).hasSize(3).contains(user1, user2, user3);
    }

    @Test
    public void itShouldFindUserById() {
        // Given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        // When
        User user = userService.findUserById(user1.getId());
        // Then
        assertThat(user1).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
        assertThat(user2).usingRecursiveComparison().ignoringFields("id").isNotEqualTo(user);
    }

    @Test
    public void itShouldTrowExceptionWhenCallingFindUserById() {
        // Given
        given(userRepository.findById(user1.getId())).willReturn(Optional.empty());
        // Then
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(user1.getId()));
    }

    @Test
    public void itShouldFindUserByUsername() {
        // Given
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));
        // When
        User user = userService.findUserByUsername(user1.getUsername());
        // Then
        assertThat(user1).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
        assertThat(user2).usingRecursiveComparison().ignoringFields("id").isNotEqualTo(user);
    }

    @Test
    public void itShouldTrowExceptionWhenCallingFindUserByUsername() {
        // Given
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());
        // Then
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUsername(user1.getUsername()));
    }

    @Test
    public void itShouldSaveUser() {
        // Given
        given(userRepository.save(user1)).willReturn(user1);
        // When
        User savedUser = userService.saveUser(user1);
        // Then
        assertThat(savedUser).isNotNull();
        assertEquals(savedUser.getUsername(), user1.getUsername());
    }

    @Test
    public void itShouldUpdateUser() {
        // Given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        // Création du DTO avec les informations mises à jour
        UserUpdateDto userUpdateDto = new UserUpdateDto("jean-petit", "jean.petit@gmail.com", null, null);

        // Création de l'objet mis à jour avec le nouveau username et email
        // C'est à partir de cet objet qu'il faudra effectuer les comparaisons : user1 ne sera pas directement modifié dans la méthode updateUser car un nouvel objet User est créé et enregistré.
        User updatedUser = User.builder()
                .id(user1.getId())
                .username("jean-petit")
                .email("jean.petit@gmail.com")
                .password(user1.getPassword())
                .roles(user1.getRoles())
                .build();

        // Simulation du mapping avec le UserMapper
        given(userMapper.toUserEntity(user1, userUpdateDto)).willReturn(updatedUser);
        given(userRepository.save(updatedUser)).willReturn(updatedUser);

        // When
        userService.updateUser(user1.getId(), userUpdateDto);

        // Then
        // Vérification que l'utilisateur a bien été mis à jour et sauvegardé
        assertThat(updatedUser.getUsername()).isEqualTo("jean-petit");
        assertThat(updatedUser.getUsername()).isNotEqualTo(user1.getUsername());
        assertThat(updatedUser.getEmail()).isEqualTo("jean.petit@gmail.com");

        // Vérifier que la méthode save a bien été appelée avec le bon utilisateur mis à jour
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        // Vérification des propriétés du user sauvegardé
        assertThat(savedUser.getUsername()).isEqualTo("jean-petit");
        assertThat(savedUser.getEmail()).isEqualTo("jean.petit@gmail.com");
    }

    @Test
    public void itShouldDeleteUserById() {
        // Given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        willDoNothing().given(userRepository).delete(user1);
        // When
        userService.findUserById(user1.getId());
        userService.deleteUserById(user1.getId());
        // Then
        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    public void itShouldBeTruthlyIfUserExistsByUsersame() {
        // Given
        given(userRepository.existsByUsername(user1.getUsername())).willReturn(true);
        // When
        boolean exists = userService.existsByUsername(user1.getUsername());
        // Then
        assertTrue(exists);
    }

    @Test
    public void itShouldBeFalsyIfUserNotExistsByUsersame() {
        // Given
        given(userRepository.existsByUsername(user1.getUsername())).willReturn(false);
        // When
        boolean exists = userService.existsByUsername(user1.getUsername());
        // Then
        assertFalse(exists);
    }

    @Test
    public void itShouldBeTruthlyIfUserExistsByEmail() {
        // Given
        given(userRepository.existsByEmail(user1.getEmail())).willReturn(true);
        // When
        boolean exists = userService.existsByEmail(user1.getEmail());
        // Then
        assertTrue(exists);
    }

    @Test
    public void itShouldBeFalsyIfUserNotExistsByEmail() {
        // Given
        given(userRepository.existsByEmail(user1.getEmail())).willReturn(false);
        // When
        boolean exists = userService.existsByEmail(user1.getEmail());
        // Then
        assertFalse(exists);
    }
}
