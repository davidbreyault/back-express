package com.david.express.service.impl;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.User;
import com.david.express.repository.UserRepository;
import com.david.express.service.UserService;
import com.david.express.web.user.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id : " + id)
        );
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
    public User updateUser(Long userId, UserUpdateDTO userUpdateDto) throws ResourceNotFoundException {
        User user = findUserById(userId);
        user.setUsername(userUpdateDto.getUsername() != null ? userUpdateDto.getUsername() : user.getUsername());
        user.setEmail(userUpdateDto.getEmail() != null ? userUpdateDto.getEmail() : user.getEmail());
        user.setPassword(userUpdateDto.getPassword() != null
                ? passwordEncoder.encode(userUpdateDto.getPassword())
                : user.getPassword());
        user.setRoles(userUpdateDto.getRoles() != null
                ? roleService.rolesAssignmentByAdmin(userUpdateDto.getRoles()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet()))
                : user.getRoles()
        );
        return userRepository.save(user);
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

    @Override
    public User getLoggedInUser() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findUserByUsername(username);
    }
}
