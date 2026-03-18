package com.dirmon.project.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    @NotBlank(message = "Agent name cannot be empty")
    private String name;

    @NotBlank(message = "Agent description cannot be empty")
    private String description;
}
