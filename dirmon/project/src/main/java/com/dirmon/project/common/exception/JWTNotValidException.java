package com.dirmon.project.common.exception;

public class JWTNotValidException extends RuntimeException {
    public JWTNotValidException(String message) {
        super(message);
    }
}
