package com.dirmon.project.user.admin.dto;

import com.dirmon.project.user.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.EnumSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Email(message = "Please enter a valid email address")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,20}$",
            message = "Password must contain at least one letter, one number, and one special character"
    )
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "Enable cannot be null")
    private Boolean enabled;

    @NotNull(message = "Account non expired cannot be null")
    private Boolean accountNonExpired;

    @NotNull(message = "Account non locked cannot be null")
    private Boolean accountNonLocked;

    @NotNull(message = "Credentials non expired cannot be null")
    private Boolean credentialsNonExpired;

    @NotNull(message = "Roles cannot be null")
    private EnumSet<UserRole> roles;
}
