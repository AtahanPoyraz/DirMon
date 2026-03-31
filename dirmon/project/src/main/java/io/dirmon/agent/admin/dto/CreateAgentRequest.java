package io.dirmon.agent.admin.dto;

import io.dirmon.agent.model.AgentConfig;
import io.dirmon.agent.model.AgentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

// TODO -> agentConfig fieldları 0 girilememeli
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
    @Builder.Default
    private AgentStatus status = AgentStatus.STATUS_INACTIVE;

    @NotNull(message = "Agent user cannot be null")
    private UUID userId;

}