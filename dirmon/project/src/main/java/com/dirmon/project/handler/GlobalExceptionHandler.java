package com.dirmon.project.handler;

import com.dirmon.project.common.dto.GenericResponse;
import com.dirmon.project.common.exception.*;
import com.dirmon.project.common.exception.AgentNotFoundException;
import com.dirmon.project.common.exception.AgentTokenException;
import com.dirmon.project.common.exception.EmailAlreadyExistException;
import com.dirmon.project.common.exception.UserNotFoundException;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(), null
        );
    }

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

    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleAgentNotFoundException(
            AgentNotFoundException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(AgentNameAlreadyExistException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleAgentNameAlreadyExistException(
            AgentNameAlreadyExistException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(AgentTokenException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleAgentTokenException(
            AgentTokenException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleBadCredentialsException(
            BadCredentialsException e
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleDataIntegrityViolationException(
            DataIntegrityViolationException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleDisabledException(
            DisabledException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleEmailAlreadyExistExceptions(
            EmailAlreadyExistException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleUserNotFoundException(
            UserNotFoundException e
    ) {
        return GenericResponse.genericResponse(
                HttpStatus.NOT_FOUND,
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull GenericResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(
                        HashMap::new,
                        (map, fieldError) -> map.put(fieldError.getField(), fieldError.getDefaultMessage()),
                        HashMap::putAll
                );

        return GenericResponse.genericResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields",
                errors
        );
    }
}
