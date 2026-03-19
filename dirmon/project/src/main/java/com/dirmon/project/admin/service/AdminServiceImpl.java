package com.dirmon.project.admin.service;

import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.agent.repository.AgentRepository;
import com.dirmon.project.common.exception.EmailAlreadyExistException;
import com.dirmon.project.common.exception.UserNotFoundException;
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
    public UserModel deleteUserByUserId(UUID userId) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        this.userRepository.delete(userEntity);
        return userEntity;
    }

    @Override
    @Transactional
    public List<UserModel> deleteUserByUserIds(List<UUID> userIds) {
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
        return userEntities;
    }
}
