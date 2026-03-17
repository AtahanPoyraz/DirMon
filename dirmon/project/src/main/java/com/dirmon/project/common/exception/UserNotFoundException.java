package com.dirmon.project.common.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(@NonNull String message) {
        super(message);
    }
}
