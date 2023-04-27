package com.david.express.web.user.mapper;

import com.david.express.entity.User;
import com.david.express.web.user.dto.UserDTO;

public class UserDTOMapper {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
