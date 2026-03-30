package io.dirmon.project.auth.exception;

import lombok.NonNull;
import org.springframework.security.authentication.BadCredentialsException;

public class CookieNotFoundException extends BadCredentialsException {
    public CookieNotFoundException(@NonNull String message) {
        super(message);
    }
}
