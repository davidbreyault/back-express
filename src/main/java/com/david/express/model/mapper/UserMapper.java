package com.david.express.model.mapper;

import com.david.express.entity.User;
import com.david.express.model.dto.UserDto;
import com.david.express.model.dto.UserUpdateDto;
import com.david.express.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

    public User toUserEntity(User user, UserUpdateDto userUpdateDto) {
        user.setUsername(userUpdateDto.getUsername() != null ? userUpdateDto.getUsername() : user.getUsername());
        user.setEmail(userUpdateDto.getEmail() != null ? userUpdateDto.getEmail() : user.getEmail());
        user.setPassword(userUpdateDto.getPassword() != null ? passwordEncoder.encode(userUpdateDto.getPassword()) : user.getPassword());
        user.setRoles(userUpdateDto.getRoles() != null
                ? roleService.assignRolesForUserByAdmin(userUpdateDto.getRoles()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet()))
                : user.getRoles()
        );
        return user;
    }
}
