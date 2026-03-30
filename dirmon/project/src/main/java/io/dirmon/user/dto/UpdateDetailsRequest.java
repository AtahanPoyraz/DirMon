package io.dirmon.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDetailsRequest {
    private String firstName;
    private String lastName;
}
