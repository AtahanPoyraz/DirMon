package io.dirmon.user.controller;

import io.dirmon.common.dto.GenericResponse;
import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.dto.UserDto;
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
        UserDto userDto = this.getUserFromSecurityContext();

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
        UserDto userDto = this.getUserFromSecurityContext();
        userDto = this.userService.updateDetailsByUserId(userDto.getUserId(), updateDetailsRequest);

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
        UserDto userDto = this.getUserFromSecurityContext();
        userDto = this.userService.updatePasswordByUserId(userDto.getUserId(), updatePasswordRequest);

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User password was updated successfully",
                userDto
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deActivateUser() {
        UserDto userDto = this.getUserFromSecurityContext();
        userDto = this.userService.deActivateUserByUserId(userDto.getUserId());

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User deactivated was updated successfully",
                userDto
        );
    }

    private UserDto getUserFromSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials not found");
        }

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        if (Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new AuthenticationCredentialsNotFoundException("Anonymous user is not allowed");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID userId)) {
            throw new BadCredentialsException("Invalid principal type");
        }

        return this.userService.fetchUserByUserId(userId);
    }
}
