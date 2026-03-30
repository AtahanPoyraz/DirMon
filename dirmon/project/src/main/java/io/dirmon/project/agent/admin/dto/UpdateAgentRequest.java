package io.dirmon.project.agent.admin.dto;

import io.dirmon.project.agent.model.AgentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentRequest {
    private String name;

    private String description;

    private AgentStatus status;

    private UUID userId;
}
