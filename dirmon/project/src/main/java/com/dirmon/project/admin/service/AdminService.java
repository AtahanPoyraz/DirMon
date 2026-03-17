package com.dirmon.project.admin.service;

import com.dirmon.project.admin.dto.CreateUserRequest;
import com.dirmon.project.admin.dto.UpdateUserRequest;
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
    List<UserModel> deleteUserByUserIds(List<UUID> userIds);

    @Transactional
    UserModel deleteUserByUserId(UUID userId);
}