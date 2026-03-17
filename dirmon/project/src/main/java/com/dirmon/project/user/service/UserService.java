package com.dirmon.project.user.service;

import com.dirmon.project.common.exception.UserNotFoundException;
import com.dirmon.project.user.dto.UpdateDetailsRequest;
import com.dirmon.project.user.dto.UpdatePasswordRequest;
import com.dirmon.project.user.model.UserModel;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface UserService {
    UserModel fetchUserByUserId(UUID userId) throws UserNotFoundException;

    @Transactional
    UserModel updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest) throws UserNotFoundException;

    @Transactional
    UserModel updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest)  throws UserNotFoundException;

    @Transactional
    UserModel deActivateUserByUserId(UUID userId) throws UserNotFoundException;
}
