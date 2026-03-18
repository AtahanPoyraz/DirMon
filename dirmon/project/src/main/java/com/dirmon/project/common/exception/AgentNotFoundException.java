package com.dirmon.project.common.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;

public class AgentNotFoundException extends EntityNotFoundException {
    public AgentNotFoundException(@NonNull String message) {
        super(message);
    }
}
