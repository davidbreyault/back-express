package com.david.express.web.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {

    private List<UserDTO> users;
    private int totalUsers;
    private Long ts;

    public UserResponseDTO(List<UserDTO> users, int totalUsers) {
        this.users = users;
        this.totalUsers = totalUsers;
        this.ts = System.currentTimeMillis();
    }
}
