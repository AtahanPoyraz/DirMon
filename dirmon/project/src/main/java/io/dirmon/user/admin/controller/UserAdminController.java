package io.dirmon.user.admin.controller;

import io.dirmon.user.admin.dto.CreateUserRequest;
import io.dirmon.user.admin.dto.UpdateUserRequest;
import io.dirmon.user.admin.dto.UserDto;
import io.dirmon.user.admin.mapper.UserAdminMapper;
import io.dirmon.user.admin.service.UserAdminService;
import io.dirmon.common.dto.GenericResponse;
import io.dirmon.user.model.UserModel;
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
            UserDto userDto = this.userAdminService.fetchUserById(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userDto
            );
        }

        if (email != null) {
            UserDto userDto = this.userAdminService.fetchUserByEmail(email);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userDto
            );
        }

        Page<@NonNull UserDto> userDtos = this.userAdminService.fetchUsers(pageable);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were fetched successfully",
                userDtos
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        UserDto userDto = this.userAdminService.createUser(createUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "User was created successfully",
                userDto
        );
    }

    @PatchMapping
    public ResponseEntity<@NonNull GenericResponse<?>> updateUser(
            @RequestParam(required = true) UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserDto userDto = this.userAdminService.updateUserByUserId(userId, updateUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was updated successfully",
                userDto
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deleteUser(
            @RequestParam(required = true) List<UUID> userId
    ) {
        if (userId.isEmpty()) {
            return GenericResponse.genericResponse(
                    HttpStatus.BAD_REQUEST,
                    "No userId provided",
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
