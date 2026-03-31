package io.dirmon.agent.admin.dto;

import io.dirmon.agent.model.AgentConfig;
import io.dirmon.agent.model.AgentStatus;
import lombok.*;

import java.util.UUID;

// TODO -> agentConfig fieldları 0 girilememeli
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentRequest {
    private String name;
    private String description;
    private AgentConfig config;
    private AgentStatus status;
    private UUID userId;
}
