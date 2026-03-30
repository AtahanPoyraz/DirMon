package com.dirmon.project.agent.admin.dto;

import com.dirmon.project.agent.model.AgentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

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

    @NotNull(message = "Agent status cannot be null")
    private AgentStatus status;

    @NotNull(message = "Agent user cannot be null")
    private UUID userId;
}