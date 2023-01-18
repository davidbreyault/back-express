package com.david.express.exception;

public class UserNotResourceOwnerException extends RuntimeException {

    public UserNotResourceOwnerException(String message) {
        super(message);
    }
}
