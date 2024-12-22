package com.david.express.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {

    private List<UserDto> users;
    private int totalUsers;
    private Long ts;

    public UserResponseDto(List<UserDto> users) {
        this.users = users;
        this.totalUsers = users.size();
        this.ts = System.currentTimeMillis();
    }
}
