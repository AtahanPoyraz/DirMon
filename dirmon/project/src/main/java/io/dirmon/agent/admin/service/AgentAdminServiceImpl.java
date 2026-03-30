package io.dirmon.agent.admin.service;

import io.dirmon.agent.admin.dto.CreateAgentRequest;
import io.dirmon.agent.admin.dto.UpdateAgentRequest;
import io.dirmon.agent.exception.AgentNotFoundException;
import io.dirmon.agent.model.AgentModel;
import io.dirmon.agent.model.AgentStatus;
import io.dirmon.agent.repository.AgentRepository;
import io.dirmon.user.exception.UserNotFoundException;
import io.dirmon.user.model.UserModel;
import io.dirmon.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentAdminServiceImpl implements AgentAdminService {
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    @Autowired
    public AgentAdminServiceImpl(
            UserRepository userRepository,
            AgentRepository agentRepository
    ) {
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public Page<@NonNull AgentModel> fetchAgents(Pageable pageable) {
        return this.agentRepository.findAll(pageable);
    }

    @Override
    public AgentModel fetchAgentById(UUID agentId) {
        return this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId));
    }

    @Override
    public List<AgentModel> fetchAgentsByUserId(UUID userId) {
        return this.agentRepository.findAllByUser_UserId(userId);
    }

    @Override
    public AgentModel fetchAgentByUserIdAndAgentId(UUID userId, UUID agentId) {
        return this.agentRepository.findByUserIdAndAgentId(userId, agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId + " and userId: " + userId));
    }

    @Override
    @Transactional
    public AgentModel createAgent(CreateAgentRequest createAgentRequest) {
        UserModel userEntity = this.userRepository.findById(createAgentRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + createAgentRequest.getUserId()));

        AgentModel agentEntity = AgentModel.builder()
                .name(createAgentRequest.getName())
                .description(createAgentRequest.getDescription())
                .status(Optional.ofNullable(createAgentRequest.getStatus()).orElse(AgentStatus.STATUS_INACTIVE))
                .user(userEntity)
                .build();

        return this.agentRepository.save(agentEntity);
    }

    @Override
    @Transactional
    public AgentModel updateAgentByAgentId(UUID agentId, UpdateAgentRequest updateAgentRequest) {
        AgentModel agentEntity = this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId));

        if (updateAgentRequest.getName() != null && !updateAgentRequest.getName().isEmpty()) {
            agentEntity.setName(updateAgentRequest.getName());
        }

        if (updateAgentRequest.getDescription() != null && !updateAgentRequest.getDescription().isEmpty()) {
            agentEntity.setDescription(updateAgentRequest.getDescription());
        }

        if (updateAgentRequest.getStatus() != null) {
            agentEntity.setStatus(updateAgentRequest.getStatus());
        }

        if (updateAgentRequest.getUserId() != null) {
            UserModel userEntity = this.userRepository.findById(updateAgentRequest.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + updateAgentRequest.getUserId()));

            agentEntity.setUser(userEntity);
        }

        return this.agentRepository.save(agentEntity);
    }

    @Override
    @Transactional
    public void deleteAgentByAgentId(UUID agentId) {
        AgentModel agentEntity = this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId));

        this.agentRepository.delete(agentEntity);
    }

    @Override
    @Transactional
    public void deleteAgentsByAgentIds(List<UUID> agentIds) {
        List<AgentModel> agentEntities = this.agentRepository.findAllById(agentIds);
        Set<UUID> foundIds = agentEntities.stream()
                .map(AgentModel::getAgentId)
                .collect(Collectors.toSet());

        agentIds.stream()
                .filter(agentId -> !foundIds.contains(agentId))
                .findFirst()
                .ifPresent(missingId -> {
                    throw new AgentNotFoundException("Agent not found with id: " + missingId);
                });

        this.agentRepository.deleteAll(agentEntities);
    }

    @Override
    @Transactional
    public void deleteAgentsByUserId(UUID userId) {
        if (this.userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        this.agentRepository.deleteAgentsByUserId(userId);
    }
}
