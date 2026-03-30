package io.dirmon.project.user.admin.dto;

import io.dirmon.project.user.model.UserRole;
import lombok.*;

import java.time.Instant;
import java.util.EnumSet;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private EnumSet<UserRole> roles;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Instant lastLogin;
    private Instant createdAt;
    private Instant updatedAt;
}
