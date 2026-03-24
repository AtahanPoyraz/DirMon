package com.dirmon.project.agent.service;

import com.dirmon.project.agent.dto.CreateAgentRequest;
import com.dirmon.project.agent.dto.UpdateAgentRequest;
import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.agent.model.AgentStatus;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface AgentService {
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
