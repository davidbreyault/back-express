package com.david.express.validation;

import com.david.express.validation.dto.ErrorResponseDTO;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {

        public static Map<String, ErrorResponseDTO> build(ErrorResponseDTO errors) {
            Map<String, ErrorResponseDTO> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);
            return errorResponse;
        }
}
