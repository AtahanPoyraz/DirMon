package com.dirmon.project.admin.controller;

import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.admin.service.UserAdminService;
import com.dirmon.project.common.dto.GenericResponse;
import com.dirmon.project.user.model.UserModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "admin-controller")
@RestController
@RequestMapping("/api/v1/admin/user")
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
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userEntity
            );
        }

        if (email != null) {
            UserModel userEntity = this.userAdminService.fetchUserByEmail(email);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userEntity
            );
        }

        Page<@NonNull UserModel> userEntities = this.userAdminService.fetchUsers(pageable);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were fetched successfully",
                userEntities
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        UserModel userEntity = this.userAdminService.createUser(createUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "User was created successfully",
                userEntity
        );
    }

    @PatchMapping
    public ResponseEntity<@NonNull GenericResponse<?>> updateUser(
            @RequestParam(required = true) UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserModel userEntity = this.userAdminService.updateUserByUserId(userId, updateUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was updated successfully",
                userEntity
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
}
