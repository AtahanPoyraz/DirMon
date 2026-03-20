package com.dirmon.project.admin.service;

import com.dirmon.project.admin.dto.agent.CreateAgentRequest;
import com.dirmon.project.admin.dto.agent.UpdateAgentRequest;
import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.user.model.UserModel;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    Page<@NonNull UserModel> fetchUsers(Pageable pageable);

    UserModel fetchUserById(UUID userId);

    UserModel fetchUserByEmail(String email);

    @Transactional
    UserModel createUser(CreateUserRequest createUserRequest);

    @Transactional
    UserModel updateUserByUserId(UUID userId, UpdateUserRequest updateUserRequest);

    @Transactional
    void deleteUserByUserId(UUID userId);

    @Transactional
    void deleteUserByUserIds(List<UUID> userIds);

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