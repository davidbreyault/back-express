package com.david.express.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthenticationDto {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters long.")
    private String username;
    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 40, message = "Password must be 6-40 characters long.")
    private String password;
}
