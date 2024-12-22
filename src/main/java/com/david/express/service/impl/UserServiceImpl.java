package com.david.express.service.impl;

import com.david.express.common.Utils;
import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.User;
import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.repository.UserRepository;
import com.david.express.service.RoleService;
import com.david.express.service.UserService;
import com.david.express.model.dto.UserDto;
import com.david.express.model.dto.UserUpdateDto;
import com.david.express.model.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;


    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public PaginatedResponseDto<UserDto> getAllUsers(int page, int size, String[] sort) {
        // Créer le Pageable avec le tri
        Pageable paging = Utils.createPaging(page, size, sort);

        // Récupération des utilisateurs
        Page<User> pageUsers = findAllUsers(paging);

        // Mapping
        List<UserDto> usersDto = pageUsers.getContent()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        // Créer la réponse avec les informations de pagination
        PaginatedResponseDto<UserDto> response = new PaginatedResponseDto<>();
        response.setKey("users");
        response.setData(usersDto);
        response.setCurrentPage(Optional.of(pageUsers).map(Page::getNumber).orElse(0));
        response.setTotalItems(Optional.of(pageUsers).map(Page::getTotalElements).orElse(0L));
        response.setTotalPages(Optional.of(pageUsers).map(Page::getTotalPages).orElse(0));

        return response;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id : " + id)
        );
    }

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(findUserById(id));
    }

    @Override
    public User findUserByUsername(String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found with username : " + username)
        );
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) throws ResourceNotFoundException {
        // Récuperer l'utilisateur à mettre à jour
        User user = findUserById(userId);
        // Mise à jour des informations
        user = userMapper.toUserEntity(user, userUpdateDto);
        // Enregistre l'utilisateur modifié
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUserById(Long id) throws ResourceNotFoundException {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
