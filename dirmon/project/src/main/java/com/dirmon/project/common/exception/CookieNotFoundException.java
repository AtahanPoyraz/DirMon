package com.dirmon.project.common.exception;

import lombok.NonNull;
import org.springframework.security.authentication.BadCredentialsException;

public class CookieNotFoundException extends BadCredentialsException {
    public CookieNotFoundException(@NonNull String message) {
        super(message);
    }
}
