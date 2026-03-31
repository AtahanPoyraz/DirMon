package io.dirmon.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// TODO -> agentConfig fieldları 0 girilememeli
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    @NotBlank(message = "Agent name cannot be empty")
    private String name;
    private String description;
}
