package com.dirmon.project.agent.service;

import com.dirmon.project.agent.dto.CreateAgentRequest;
import com.dirmon.project.agent.dto.UpdateAgentRequest;
import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.agent.model.AgentStatus;
import com.dirmon.project.agent.repository.AgentRepository;
import com.dirmon.project.common.exception.AgentNameAlreadyExistException;
import com.dirmon.project.common.exception.AgentNotFoundException;
import com.dirmon.project.common.exception.UserNotFoundException;
import com.dirmon.project.user.model.UserModel;
import com.dirmon.project.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @Autowired
    public AgentServiceImpl(
            AgentRepository agentRepository,
            UserRepository userRepository
    ) {
        this.agentRepository = agentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AgentModel fetchAgentByAgentId(UUID agentId) {
        return this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id " + agentId));
    }

    @Override
    public List<AgentModel> fetchAllAgentsByUserId(UUID userId) {
        return this.agentRepository.findAllByUser_UserId(userId);
    }

    @Override
    public AgentModel fetchAgentByUserIdAndAgentId(UUID userId, UUID agentId) {
        return this.agentRepository.findByUserIdAndAgentId(userId, agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId + " and userId: " + userId));
    }

    @Override
    @Transactional
    public AgentModel createAgentByUserIdAndAgentId(UUID userId, CreateAgentRequest createAgentRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (this.agentRepository.findByName(createAgentRequest.getName()).isPresent()) {
            throw new AgentNameAlreadyExistException("Agent already exists with name '" + createAgentRequest.getName());
        }

        AgentModel agentModel = AgentModel.builder()
                .name(createAgentRequest.getName())
                .description(createAgentRequest.getDescription())
                .status(AgentStatus.STATUS_INACTIVE)
                .user(userEntity)
                .build();

        return this.agentRepository.save(agentModel);
    }

    @Override
    @Transactional
    public AgentModel updateAgentDetailsByUserIdAndAgentId(UUID userId, UUID agentId, UpdateAgentRequest updateAgentRequest) {
        AgentModel agentEntity = this.agentRepository.findByUserIdAndAgentId(userId, agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId + " and userId: " + userId));

        if (updateAgentRequest.getName() != null && !updateAgentRequest.getName().isEmpty()) {
            agentEntity.setName(updateAgentRequest.getName());
        }

        if (updateAgentRequest.getDescription() != null && !updateAgentRequest.getDescription().isEmpty()) {
            agentEntity.setDescription(updateAgentRequest.getDescription());
        }

        return this.agentRepository.save(agentEntity);
    }

    @Override
    @Transactional
    public AgentModel activateAgentByAgentId(UUID agentId) {
        AgentModel agentEntity = this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + agentId));

        agentEntity.setStatus(AgentStatus.STATUS_ACTIVE);
        return this.agentRepository.save(agentEntity);
    }

    @Override
    @Transactional
    public void deleteAgentByUserIdAndAgentId(UUID userId, UUID agentId) {
        AgentModel agentModel = this.agentRepository.findByUserIdAndAgentId(userId, agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with userId: " + userId + " and id: " + agentId));

        this.agentRepository.delete(agentModel);
    }

    @Transactional
    public void deleteAllAgentsByUserIdAndAgentIds(UUID userId, List<UUID> agentIds) {
        List<AgentModel> agentEntities = this.agentRepository.findAllByUser_UserIdAndAgentIdIn(userId, agentIds);
        Set<UUID> foundIds = agentEntities.stream()
                .map(AgentModel::getAgentId)
                .collect(Collectors.toSet());

        agentIds.stream()
                .filter(agentId -> !foundIds.contains(agentId))
                .findFirst()
                .ifPresent(missingId -> {
                    throw new AgentNotFoundException("Agent not found for this user with id: " + missingId);
                });

        this.agentRepository.deleteAll(agentEntities);
    }
}
