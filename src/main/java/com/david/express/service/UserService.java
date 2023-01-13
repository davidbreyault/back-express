package com.david.express.service;

import com.david.express.exception.UserNotFoundException;
import com.david.express.model.User;

import java.util.List;

public interface UserService {
    public List<User> findAllUsers();
    public User findUserById(Long id) throws UserNotFoundException;
}
