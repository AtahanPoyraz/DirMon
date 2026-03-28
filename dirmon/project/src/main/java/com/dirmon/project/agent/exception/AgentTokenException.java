package com.dirmon.project.agent.exception;

import jakarta.persistence.EntityNotFoundException;

public class AgentTokenException extends EntityNotFoundException {
    public AgentTokenException(String message) {
        super(message);
    }
}
