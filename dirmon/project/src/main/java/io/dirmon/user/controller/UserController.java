package io.dirmon.user.controller;

import io.dirmon.common.dto.GenericResponse;
import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.dto.UserDto;
import io.dirmon.user.mapper.UserMapper;
import io.dirmon.user.model.UserModel;
import io.dirmon.user.service.UserService;
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
    private final UserMapper userMapper;

    @Autowired
    public UserController(
            UserService userService,
            UserMapper userMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchUser() {
        UserModel userEntity = this.getUserFromSecurityContext();
        UserDto userDto = this.userMapper.toDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was fetched successfully",
                userDto
        );
    }

    @PatchMapping("/details")
    public ResponseEntity<@NonNull GenericResponse<?>> updateUserDetails(
            @Valid @RequestBody UpdateDetailsRequest updateDetailsRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.updateDetailsByUserId(userEntity.getUserId(), updateDetailsRequest);

        UserDto userDto = this.userMapper.toDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User details were updated successfully",
                userDto
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<@NonNull GenericResponse<?>> updateUserPassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.updatePasswordByUserId(userEntity.getUserId(), updatePasswordRequest);

        UserDto userDto = this.userMapper.toDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User password was updated successfully",
                userDto
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deActivateUser() {
        UserModel userEntity = this.getUserFromSecurityContext();
        userEntity = this.userService.deActivateUserByUserId(userEntity.getUserId());

        UserDto userDto = this.userMapper.toDto(userEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User deactivated was updated successfully",
                userDto
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
}
