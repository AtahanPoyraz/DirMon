package io.dirmon.auth.exception;

import lombok.NonNull;

public class AuthenticationException extends IllegalArgumentException {
    public AuthenticationException(@NonNull String message) {
        super(message);
    }
}
