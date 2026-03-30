package io.dirmon.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,20}$",
            message = "Password must contain at least one letter, one number, and one special character"
    )
    @NotBlank(message = "New Password cannot be empty")
    private String newPassword;
}
