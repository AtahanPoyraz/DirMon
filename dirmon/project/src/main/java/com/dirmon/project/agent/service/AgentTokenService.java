package com.dirmon.project.agent.service;

import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.common.exception.AgentNotFoundException;
import com.dirmon.project.common.exception.AgentTokenException;
import lombok.NonNull;

public interface AgentTokenService {
    String generateActivationToken(@NonNull AgentModel agentEntity);
    String generateHeartbeatToken(@NonNull AgentModel agentEntity);

    AgentModel extractAndVerifyToken(String token) throws AgentNotFoundException, AgentTokenException;
}
