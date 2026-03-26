package com.dirmon.project.common.exception;

public class AgentNameAlreadyExistException extends IllegalStateException {
    public AgentNameAlreadyExistException(String message) {
        super(message);
    }
}
