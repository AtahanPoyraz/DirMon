package io.dirmon.user.admin.service;

import io.dirmon.user.admin.dto.CreateUserRequest;
import io.dirmon.user.admin.dto.UpdateUserRequest;
import io.dirmon.user.admin.dto.UserDto;
import io.dirmon.user.exception.EmailAlreadyExistException;
import io.dirmon.user.exception.UserNotFoundException;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserAdminService {
    Page<@NonNull UserDto> fetchUsers(Pageable pageable);

    UserDto fetchUserById(UUID userId) throws UserNotFoundException;

    UserDto fetchUserByEmail(String email) throws UserNotFoundException;

    @Transactional
    UserDto createUser(CreateUserRequest createUserRequest) throws EmailAlreadyExistException;

    @Transactional
    UserDto updateUserByUserId(UUID userId, UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailAlreadyExistException;

    @Transactional
    void deleteUserByUserId(UUID userId) throws UserNotFoundException;

    @Transactional
    void deleteUserByUserIds(List<UUID> userIds) throws UserNotFoundException;
}
