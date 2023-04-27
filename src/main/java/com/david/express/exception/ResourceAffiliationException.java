package com.david.express.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceAffiliationException extends RuntimeException {

    public ResourceAffiliationException(String message) {
        super(message);
    }
}
