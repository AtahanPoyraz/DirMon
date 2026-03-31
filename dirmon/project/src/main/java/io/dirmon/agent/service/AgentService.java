package io.dirmon.agent.service;

import io.dirmon.agent.dto.CreateAgentRequest;
import io.dirmon.agent.dto.UpdateAgentConfigRequest;
import io.dirmon.agent.dto.UpdateAgentDetailsRequest;
import io.dirmon.agent.model.AgentModel;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface AgentService {
    AgentModel fetchAgentByAgentId(UUID agentId);
    List<AgentModel> fetchAllAgentsByUserId(UUID userId);
    AgentModel fetchAgentByUserIdAndAgentId(UUID userId, UUID agentId);

    @Transactional
    AgentModel createAgentByUserId(UUID userId, CreateAgentRequest createAgentRequest);

    @Transactional
    AgentModel updateAgentDetailsByUserIdAndAgentId(UUID userId, UUID agentId, UpdateAgentDetailsRequest updateAgentDetailsRequest);

    @Transactional
    AgentModel updateAgentConfigByUserIdAndAgentId(UUID userId, UUID agentId, UpdateAgentConfigRequest updateAgentConfigRequest);

    @Transactional
    AgentModel activateAgentByAgentId(UUID agentId);

    @Transactional
    void deleteAgentByUserIdAndAgentId(UUID userId, UUID agentId);

    @Transactional
    void deleteAllAgentsByUserIdAndAgentIds(UUID userId, List<UUID> agentIds);
}
