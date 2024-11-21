package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.User;
import com.david.express.web.user.dto.UserDTO;
import com.david.express.web.user.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findUserById(Long id) throws ResourceNotFoundException;
    User findUserByUsername(String username) throws ResourceNotFoundException;
    User saveUser(User user);
    User updateUser(Long userId, UserUpdateDTO userDto) throws ResourceNotFoundException;
    void deleteUserById(Long id) throws ResourceNotFoundException;
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User getLoggedInUser() throws ResourceNotFoundException;
}
