package com.dirmon.project.admin.service;

import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.user.model.UserModel;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserAdminService {
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
}
