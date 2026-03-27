package com.dirmon.project.user.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(@NonNull String message) {
        super(message);
    }
}
