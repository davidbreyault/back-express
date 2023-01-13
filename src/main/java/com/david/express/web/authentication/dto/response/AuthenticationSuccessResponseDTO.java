package com.david.express.web.authentication.dto.response;

import lombok.Data;

@Data
public class AuthenticationSuccessResponseDTO {

    private String accessToken;

    public AuthenticationSuccessResponseDTO(String token) {
        this.accessToken = token;
    }
}
