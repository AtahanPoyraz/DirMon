package io.dirmon.project.agent.service;

import io.dirmon.project.agent.model.AgentModel;
import lombok.NonNull;

public interface AgentTokenService {
    String generateActivationToken(@NonNull AgentModel agentEntity);
    String generateHeartbeatToken(@NonNull AgentModel agentEntity);

    AgentModel extractAndVerifyToken(String token);
}
