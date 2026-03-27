package com.dirmon.project.admin.service;

import com.dirmon.project.admin.dto.agent.CreateAgentRequest;
import com.dirmon.project.admin.dto.agent.UpdateAgentRequest;
import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.agent.model.AgentStatus;
import com.dirmon.project.agent.repository.AgentRepository;
import com.dirmon.project.agent.exception.AgentNotFoundException;
import com.dirmon.project.user.exception.EmailAlreadyExistException;
import com.dirmon.project.user.exception.UserNotFoundException;
import com.dirmon.project.user.model.UserModel;
import com.dirmon.project.user.model.UserRole;
import com.dirmon.project.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(
            UserRepository userRepository,
            AgentRepository agentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<@NonNull UserModel> fetchUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public UserModel fetchUserById(@NonNull UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public UserModel fetchUserByEmail(@NonNull String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserModel createUser(CreateUserRequest createUserRequest) {
        if (this.userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExistException("User with email " + createUserRequest.getEmail() + " already exists");
        }

        UserModel userEntity = UserModel.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .email(createUserRequest.getEmail())
                .password(this.passwordEncoder.encode(createUserRequest.getPassword()))
                .roles(Optional.ofNullable(createUserRequest.getRoles()).map(EnumSet::copyOf).orElse(EnumSet.of(UserRole.ROLE_USER)))
                .enabled(createUserRequest.getEnabled() != null ? createUserRequest.getEnabled() : true)
                .accountNonExpired(createUserRequest.getAccountNonExpired() != null ? createUserRequest.getAccountNonExpired() : true)
                .accountNonLocked(createUserRequest.getAccountNonLocked() != null ? createUserRequest.getAccountNonLocked() : true)
                .credentialsNonExpired(createUserRequest.getCredentialsNonExpired() != null ? createUserRequest.getCredentialsNonExpired() : true)
                .build();

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel updateUserByUserId(UUID userId, UpdateUserRequest updateUserRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().isEmpty()) {
            userEntity.setFirstName(updateUserRequest.getFirstName());
        }

        if (updateUserRequest.getLastName() != null && !updateUserRequest.getLastName().isEmpty()) {
            userEntity.setLastName(updateUserRequest.getLastName());
        }

        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            if (this.userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new EmailAlreadyExistException("User with email " + updateUserRequest.getEmail() + " already exists");
            }

            userEntity.setEmail(updateUserRequest.getEmail());
        }

        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            userEntity.setPassword(this.passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        if (updateUserRequest.getRoles() != null) {
            userEntity.setRoles(EnumSet.copyOf(updateUserRequest.getRoles()));
        }

        if (updateUserRequest.getEnabled() != null) {
            userEntity.setEnabled(updateUserRequest.getEnabled());
        }

        if (updateUserRequest.getAccountNonExpired() != null) {
            userEntity.setAccountNonExpired(updateUserRequest.getAccountNonExpired());
        }

        if (updateUserRequest.getAccountNonLocked() != null) {
            userEntity.setAccountNonLocked(updateUserRequest.getAccountNonLocked());
        }

        if (updateUserRequest.getCredentialsNonExpired() != null) {
            userEntity.setCredentialsNonExpired(updateUserRequest.getCredentialsNonExpired());
        }

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void deleteUserByUserId(UUID userId) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        this.userRepository.delete(userEntity);
    }

    @Override
    @Transactional
    public void deleteUserByUserIds(List<UUID> userIds) {
        List<UserModel> userEntities = this.userRepository.findAllById(userIds);
        Set<UUID> foundIds = userEntities.stream()
                .map(UserModel::getUserId)
                .collect(Collectors.toSet());

        userIds.stream()
                .filter(userId -> !foundIds.contains(userId))
                .findFirst()
                .ifPresent(missingId -> {
                    throw new UserNotFoundException("User not found with id: " + missingId);
                });

        this.userRepository.deleteAll(userEntities);
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
