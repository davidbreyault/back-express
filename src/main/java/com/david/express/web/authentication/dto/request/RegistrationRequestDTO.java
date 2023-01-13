package com.david.express.web.authentication.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class RegistrationRequestDTO {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters long.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Size(max = 50, message = "Email must not be over 50 characters long.")
    @Email(message = "Please add a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 40, message = "Username must be 6-40 characters long.")
    private String password;

    private Set<String> roles;
}
