package com.david.express.service;

import com.david.express.exception.ResourceNotFoundException;
import com.david.express.entity.User;
import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.model.dto.UserDto;
import com.david.express.model.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> findAllUsers(Pageable pageable);
    PaginatedResponseDto<UserDto> getAllUsers(int page, int size, String[] sort);
    User findUserById(Long id) throws ResourceNotFoundException;
    UserDto getUserById(Long id);
    User findUserByUsername(String username) throws ResourceNotFoundException;
    User saveUser(User user);
    UserDto updateUser(Long userId, UserUpdateDto userDto) throws ResourceNotFoundException;
    void deleteUserById(Long id) throws ResourceNotFoundException;
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
