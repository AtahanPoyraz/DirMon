package com.dirmon.project.agent.service;

import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.common.exception.AgentTokenNotValidException;
import io.jsonwebtoken.Claims;
import lombok.NonNull;

import java.time.Instant;

public interface AgentTokenService {
    String generateAgentToken(@NonNull AgentModel agentEntity);
    Claims validateAgentToken(String agentToken) throws AgentTokenNotValidException;

    String extractId(String token) throws AgentTokenNotValidException;
    String extractSubject(String token) throws AgentTokenNotValidException;
    Instant extractExpiration(String token) throws AgentTokenNotValidException;
}
