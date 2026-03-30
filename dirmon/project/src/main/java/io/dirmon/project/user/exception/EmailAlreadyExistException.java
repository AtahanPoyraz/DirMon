package io.dirmon.project.user.exception;

import lombok.NonNull;

public class EmailAlreadyExistException extends IllegalStateException {
    public EmailAlreadyExistException(@NonNull String message) {
        super(message);
    }
}
