package io.dirmon.agent.admin.dto;

import io.dirmon.agent.model.AgentStatus;
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
    private UUID userId;
    private String name;
    private String description;
    private AgentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
