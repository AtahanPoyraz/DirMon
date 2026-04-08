package io.dirmon.user.service;

import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.dto.UserDto;
import io.dirmon.user.exception.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {
    UserDto fetchUserByUserId(UUID userId) throws UserNotFoundException;

    @Transactional
    UserDto updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest) throws UserNotFoundException;

    @Transactional
    UserDto updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest) throws UserNotFoundException, BadCredentialsException;

    @Transactional
    UserDto deActivateUserByUserId(UUID userId) throws UserNotFoundException;
}
