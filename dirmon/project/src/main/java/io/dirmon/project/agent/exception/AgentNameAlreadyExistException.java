package io.dirmon.project.agent.exception;

public class AgentNameAlreadyExistException extends IllegalStateException {
    public AgentNameAlreadyExistException(String message) {
        super(message);
    }
}
