package com.david.express.validation;

import com.david.express.validation.dto.ErrorResponseDto;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {

        public static Map<String, ErrorResponseDto> build(ErrorResponseDto errors) {
            Map<String, ErrorResponseDto> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);
            return errorResponse;
        }
}
