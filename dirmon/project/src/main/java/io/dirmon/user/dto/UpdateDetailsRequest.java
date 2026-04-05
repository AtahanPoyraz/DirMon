package io.dirmon.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDetailsRequest {
    private String firstName;
    private String lastName;

    @Email(message = "")
    private String email;
}
