package io.dirmon.user.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER,
    ROLE_STAFF,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}