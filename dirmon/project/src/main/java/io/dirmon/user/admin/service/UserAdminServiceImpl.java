package io.dirmon.user.admin.service;

import io.dirmon.user.admin.dto.CreateUserRequest;
import io.dirmon.user.admin.dto.UpdateUserRequest;
import io.dirmon.user.admin.mapper.UserAdminMapper;
import io.dirmon.user.exception.EmailAlreadyExistException;
import io.dirmon.user.exception.UserNotFoundException;
import io.dirmon.user.model.UserModel;
import io.dirmon.user.model.UserRole;
import io.dirmon.user.repository.UserRepository;
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
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAdminMapper userAdminMapper;

    @Autowired
    public UserAdminServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserAdminMapper userAdminMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAdminMapper = userAdminMapper;
    }

    @Override
    public Page<@NonNull UserModel> fetchUsers(@NonNull Pageable pageable) {
        return this.userRepository.findAll(pageable);
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
            throw new EmailAlreadyExistException("User already exists with email " + createUserRequest.getEmail());
        }

        UserModel userEntity = this.userAdminMapper.toEntity(createUserRequest);
        userEntity.setPassword(this.passwordEncoder.encode(createUserRequest.getPassword()));

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel updateUserByUserId(UUID userId, UpdateUserRequest updateUserRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        this.userAdminMapper.updateUserToEntity(updateUserRequest, userEntity);

        String newEmail = updateUserRequest.getEmail();
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(userEntity.getEmail())) {
            if (this.userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistException("User already exists with email " + newEmail);
            }

            userEntity.setEmail(newEmail);
        }

        String newPassword = updateUserRequest.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            userEntity.setPassword(this.passwordEncoder.encode(newPassword));
        }

        if (updateUserRequest.getRoles() != null) {
            userEntity.setRoles(EnumSet.copyOf(updateUserRequest.getRoles()));
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
}
