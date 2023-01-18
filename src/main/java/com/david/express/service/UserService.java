package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.User;

import java.util.List;

public interface UserService {
    public List<User> findAllUsers();
    public User findUserById(Long id) throws ResourceNotFoundException;
    public User findUserByUsername(String username) throws ResourceNotFoundException;
    public void deleteUserById(Long id) throws ResourceNotFoundException;
}
