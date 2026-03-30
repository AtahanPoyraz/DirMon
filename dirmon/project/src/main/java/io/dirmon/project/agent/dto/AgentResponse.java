package io.dirmon.project.agent.dto;

import io.dirmon.project.agent.model.AgentStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private UUID agentId;
    private String name;
    private String description;
    private AgentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
