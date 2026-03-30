package io.dirmon.auth.exception;

public class JWTNotValidException extends RuntimeException {
    public JWTNotValidException(String message) {
        super(message);
    }
}
