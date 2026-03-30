package io.dirmon.agent.handler;

import io.dirmon.agent.exception.AgentNameAlreadyExistException;
import io.dirmon.agent.exception.AgentNotFoundException;
import io.dirmon.agent.exception.AgentTokenException;
import io.dirmon.common.dto.GenericResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AgentExceptionHandler {
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
}
