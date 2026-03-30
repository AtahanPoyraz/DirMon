package com.dirmon.project.user.controller;

import com.dirmon.project.common.dto.GenericResponse;
import com.dirmon.project.user.dto.UpdateDetailsRequest;
import com.dirmon.project.user.dto.UpdatePasswordRequest;
import com.dirmon.project.user.dto.UserResponse;
import com.dirmon.project.user.model.UserModel;
import com.dirmon.project.user.service.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchUser() {
        UserModel userEntity = this.getUserFromSecurityContext();
        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was fetched successfully",
                userResponse
        );
    }

    @PatchMapping("/details")
    public ResponseEntity<@NonNull GenericResponse<?>> updateUserDetails(
            @Valid @RequestBody UpdateDetailsRequest updateDetailsRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.updateDetailsByUserId(userEntity.getUserId(), updateDetailsRequest);

        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User details were updated successfully",
                userResponse
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<@NonNull GenericResponse<?>> updateUserPassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.updatePasswordByUserId(userEntity.getUserId(), updatePasswordRequest);

        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User password was updated successfully",
                userResponse
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deActivateUser() {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.deActivateUserByUserId(userEntity.getUserId());

        UserResponse userResponse = convertEntityToDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User deactivated was updated successfully",
                userResponse
        );
    }

    private UserModel getUserFromSecurityContext() throws AuthenticationCredentialsNotFoundException, BadCredentialsException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials not found");
        }

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials not found");
        }

        if (Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials not found");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID userId)) {
            throw new BadCredentialsException("Invalid principal type");
        }

        return this.userService.fetchUserByUserId(userId);
    }

    private static UserResponse convertEntityToDto(UserModel userEntity) {
        return UserResponse.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .roles(EnumSet.copyOf(userEntity.getRoles()))
                .lastLogin(userEntity.getLastLogin())
                .createdAt(userEntity.getUpdatedAt())
                .build();
    }
}
