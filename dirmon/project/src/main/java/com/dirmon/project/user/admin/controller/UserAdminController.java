package com.dirmon.project.user.admin.controller;

import com.dirmon.project.user.admin.dto.CreateUserRequest;
import com.dirmon.project.user.admin.dto.UpdateUserRequest;
import com.dirmon.project.user.admin.dto.UserResponse;
import com.dirmon.project.user.admin.service.UserAdminService;
import com.dirmon.project.common.dto.GenericResponse;
import com.dirmon.project.user.model.UserModel;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/admin")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @Autowired
    public UserAdminController(
            UserAdminService userAdminService
    ) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchUser(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        if (userId != null) {
            UserModel userEntity = this.userAdminService.fetchUserById(userId);
            UserResponse userResponse = convertEntityToDto(userEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userResponse
            );
        }

        if (email != null) {
            UserModel userEntity = this.userAdminService.fetchUserByEmail(email);
            UserResponse userResponse = convertEntityToDto(userEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userResponse
            );
        }

        Page<@NonNull UserModel> userEntities = this.userAdminService.fetchUsers(pageable);
        Page<@NonNull UserResponse> userResponses = userEntities.map(UserAdminController::convertEntityToDto);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were fetched successfully",
                userResponses
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        UserModel userEntity = this.userAdminService.createUser(createUserRequest);
        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "User was created successfully",
                userResponse
        );
    }

    @PatchMapping
    public ResponseEntity<@NonNull GenericResponse<?>> updateUser(
            @RequestParam(required = true) UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserModel userEntity = this.userAdminService.updateUserByUserId(userId, updateUserRequest);
        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was updated successfully",
                userResponse
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deleteUser(
            @RequestParam(required = true) List<UUID> userId
    ) {
        if (userId.isEmpty()) {
            return GenericResponse.genericResponse(
                    HttpStatus.BAD_REQUEST,
                    "No userIds provided",
                    null
            );
        }

        if (userId.size() == 1) {
            this.userAdminService.deleteUserByUserId(userId.getFirst());
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was deleted successfully",
                    null
            );
        }

        this.userAdminService.deleteUserByUserIds(userId);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were deleted successfully",
                null
        );
    }

    private static UserResponse convertEntityToDto(UserModel userEntity) {
        return UserResponse.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(EnumSet.copyOf(userEntity.getRoles()))
                .enabled(userEntity.isEnabled())
                .accountNonExpired(userEntity.isAccountNonExpired())
                .accountNonLocked(userEntity.isAccountNonLocked())
                .credentialsNonExpired(userEntity.isCredentialsNonExpired())
                .lastLogin(userEntity.getLastLogin())
                .createdAt(userEntity.getUpdatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }
}
