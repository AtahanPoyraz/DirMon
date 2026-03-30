package com.dirmon.project.auth.handler;

import com.dirmon.project.auth.exception.AuthenticationException;
import com.dirmon.project.auth.exception.CookieNotFoundException;
import com.dirmon.project.auth.exception.JWTNotValidException;
import com.dirmon.project.common.dto.GenericResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleAuthenticationExceptions(
            AuthenticationException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(CookieNotFoundException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleCookieNotFoundException(
            CookieNotFoundException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(JWTNotValidException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleJWTNotValidException(
            JWTNotValidException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                null
        );
    }
}
