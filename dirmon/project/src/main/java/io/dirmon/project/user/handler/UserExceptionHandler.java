package io.dirmon.project.user.handler;

import io.dirmon.project.common.dto.GenericResponse;
import io.dirmon.project.user.exception.EmailAlreadyExistException;
import io.dirmon.project.user.exception.UserNotFoundException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

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
}
