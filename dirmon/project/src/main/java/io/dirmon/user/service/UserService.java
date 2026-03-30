package io.dirmon.user.service;

import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.model.UserModel;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface UserService {
    UserModel fetchUserByUserId(UUID userId);

    @Transactional
    UserModel updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest);

    @Transactional
    UserModel updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest);

    @Transactional
    UserModel deActivateUserByUserId(UUID userId);
}
