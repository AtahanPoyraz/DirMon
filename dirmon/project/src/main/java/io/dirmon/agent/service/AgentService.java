package io.dirmon.agent.service;

import io.dirmon.agent.dto.CreateAgentRequest;
import io.dirmon.agent.dto.UpdateAgentRequest;
import io.dirmon.agent.model.AgentModel;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface AgentService {
    AgentModel fetchAgentByAgentId(UUID agentId);
    List<AgentModel> fetchAllAgentsByUserId(UUID userId);
    AgentModel fetchAgentByUserIdAndAgentId(UUID userId, UUID agentId);

    @Transactional
    AgentModel createAgentByUserIdAndAgentId(UUID userId, CreateAgentRequest createAgentRequest);

    @Transactional
    AgentModel updateAgentDetailsByUserIdAndAgentId(UUID userId, UUID agentId, UpdateAgentRequest updateAgentRequest);

    @Transactional
    AgentModel activateAgentByAgentId(UUID agentId);

    @Transactional
    void deleteAgentByUserIdAndAgentId(UUID userId, UUID agentId);

    @Transactional
    void deleteAllAgentsByUserIdAndAgentIds(UUID userId, List<UUID> agentIds);
}
