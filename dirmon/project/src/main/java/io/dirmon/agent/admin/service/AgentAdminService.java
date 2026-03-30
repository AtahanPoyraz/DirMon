package io.dirmon.agent.admin.service;

import io.dirmon.agent.admin.dto.CreateAgentRequest;
import io.dirmon.agent.admin.dto.UpdateAgentRequest;
import io.dirmon.agent.model.AgentModel;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AgentAdminService {
    Page<@NonNull AgentModel> fetchAgents(Pageable pageable);

    AgentModel fetchAgentById(UUID agentId);

    List<AgentModel> fetchAgentsByUserId(UUID userId);

    AgentModel fetchAgentByUserIdAndAgentId(UUID userId, UUID agentId);

    @Transactional
    AgentModel createAgent(CreateAgentRequest createAgentRequest);

    @Transactional
    AgentModel updateAgentByAgentId(UUID agentId, UpdateAgentRequest updateAgentRequest);

    @Transactional
    void deleteAgentByAgentId(UUID agentId);

    @Transactional
    void deleteAgentsByAgentIds(List<UUID> agentIds);

    @Transactional
    void deleteAgentsByUserId(UUID userId);
}
