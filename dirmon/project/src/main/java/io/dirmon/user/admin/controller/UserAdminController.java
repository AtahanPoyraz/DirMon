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
    private final UserAdminMapper userAdminMapper;

    @Autowired
    public UserAdminController(
            UserAdminService userAdminService,
            UserAdminMapper userAdminMapper
    ) {
        this.userAdminService = userAdminService;
        this.userAdminMapper = userAdminMapper;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchUser(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        if (userId != null) {
            UserModel userEntity = this.userAdminService.fetchUserById(userId);
            UserDto userDto = this.userAdminMapper.toDto(userEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userDto
            );
        }

        if (email != null) {
            UserModel userEntity = this.userAdminService.fetchUserByEmail(email);
            UserDto userDto = this.userAdminMapper.toDto(userEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userDto
            );
        }

        Page<@NonNull UserModel> userEntities = this.userAdminService.fetchUsers(pageable);
        Page<@NonNull UserDto> userResponses = userEntities.map(this.userAdminMapper::toDto);
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
        UserDto userDto = this.userAdminMapper.toDto(userEntity);
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
        UserModel userEntity = this.userAdminService.updateUserByUserId(userId, updateUserRequest);
        UserDto userDto = this.userAdminMapper.toDto(userEntity);
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
