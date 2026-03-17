package com.dirmon.project.common.exception;

import lombok.NonNull;

public class EmailAlreadyExistException extends IllegalStateException {
    public EmailAlreadyExistException(@NonNull String message) {
        super(message);
    }
}
