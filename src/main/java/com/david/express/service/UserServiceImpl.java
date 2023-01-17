package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.model.User;
import com.david.express.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
    public void deleteUserById(Long id) throws ResourceNotFoundException {
        User user = findUserById(id);
        userRepository.deleteById(user.getId());
    }
}
