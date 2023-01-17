package com.david.express.web.authentication.dto.response;

import lombok.Data;

@Data
public class AuthenticationJwtResponseDTO {

    private String accessToken;

    public AuthenticationJwtResponseDTO(String token) {
        this.accessToken = token;
    }
}
