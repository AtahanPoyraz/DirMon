package io.dirmon.agent.service;

import io.dirmon.agent.model.AgentModel;
import lombok.NonNull;

public interface AgentTokenService {
    String generateActivationToken(@NonNull AgentModel agentEntity);
    String generateHeartbeatToken(@NonNull AgentModel agentEntity);

    AgentModel extractAndVerifyToken(String token);
}
