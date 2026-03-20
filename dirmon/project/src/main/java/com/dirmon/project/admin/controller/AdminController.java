package com.dirmon.project.admin.controller;

import com.dirmon.project.admin.dto.agent.CreateAgentRequest;
import com.dirmon.project.admin.dto.agent.UpdateAgentRequest;
import com.dirmon.project.admin.dto.user.CreateUserRequest;
import com.dirmon.project.admin.dto.user.UpdateUserRequest;
import com.dirmon.project.admin.service.AdminService;
import com.dirmon.project.agent.model.AgentModel;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }

    @GetMapping("/user")
    public ResponseEntity<@NonNull GenericResponse<?>> fetchUser(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        if (userId != null) {
            UserModel userEntity = this.adminService.fetchUserById(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userEntity
            );
        }

        if (email != null) {
            UserModel userEntity = this.adminService.fetchUserByEmail(email);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was fetched successfully",
                    userEntity
            );
        }

        Page<@NonNull UserModel> userEntities = this.adminService.fetchUsers(pageable);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were fetched successfully",
                userEntities
        );
    }

    @PostMapping("/user")
    public ResponseEntity<@NonNull GenericResponse<?>> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        UserModel userEntity = this.adminService.createUser(createUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "User was created successfully",
                userEntity
        );
    }

    @PatchMapping("/user")
    public ResponseEntity<@NonNull GenericResponse<?>> updateUser(
            @RequestParam(required = true) UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserModel userEntity = this.adminService.updateUserByUserId(userId, updateUserRequest);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was updated successfully",
                userEntity
        );
    }

    @DeleteMapping("/user")
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
            this.adminService.deleteUserByUserId(userId.getFirst());
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "User was deleted successfully",
                    null
            );
        }

        this.adminService.deleteUserByUserIds(userId);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were deleted successfully",
                null
        );
    }

    @GetMapping("/agent")
    public ResponseEntity<@NonNull GenericResponse<?>> fetchAgent(
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) UUID userId,
            @ParameterObject Pageable pageable
    ) {
        if (userId != null && agentId != null) {
            AgentModel agentEntity = this.adminService.fetchAgentByUserIdAndAgentId(userId, agentId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentEntity
            );
        }

        if (agentId != null) {
            AgentModel agentEntity = this.adminService.fetchAgentById(agentId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentEntity
            );
        }

        if (userId != null) {
            List<AgentModel> agentEntity = this.adminService.fetchAgentsByUserId(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agents were fetched successfully",
                    agentEntity
            );
        }

        Page<@NonNull AgentModel> agentEntities = this.adminService.fetchAgents(pageable);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were fetched successfully",
                agentEntities
        );
    }

    @PostMapping("/agent")
    public ResponseEntity<@NonNull GenericResponse<?>> createAgent(
            @Valid @RequestBody CreateAgentRequest createAgentRequest
    ) {
        AgentModel agentEntity = this.adminService.createAgent(createAgentRequest);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "Agent was created successfully",
                agentEntity
        );
    }

    @PatchMapping("/agent")
    public ResponseEntity<@NonNull GenericResponse<?>> updateAgent(
            @RequestParam(required = true) UUID agentId,
            @Valid @RequestBody UpdateAgentRequest updateAgentRequest
    ) {
        AgentModel agentEntity = this.adminService.updateAgentByAgentId(agentId, updateAgentRequest);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent was updated successfully",
                agentEntity
        );
    }

    @DeleteMapping("/agent")
    public ResponseEntity<@NonNull GenericResponse<?>> deleteAgent(
            @RequestParam(required = false) List<UUID> agentId,
            @RequestParam(required = false) UUID userId
    ) {
        if (userId != null && agentId != null) {
            return GenericResponse.genericResponse(
                    HttpStatus.BAD_REQUEST,
                    "Provide either userId or agentIds, not both",
                    null
            );
        }

        if ((agentId == null || agentId.isEmpty()) && userId == null) {
            return GenericResponse.genericResponse(
                    HttpStatus.BAD_REQUEST,
                    "No agentIds or userId provided",
                    null
            );
        }

        if (userId != null) {
            this.adminService.deleteAgentsByUserId(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agents were deleted successfully for userId",
                    null
            );
        }

        if (agentId.size() == 1) {
            this.adminService.deleteAgentByAgentId(agentId.getFirst());
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was deleted successfully",
                    null
            );
        }

        this.adminService.deleteAgentsByAgentIds(agentId);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were deleted successfully",
                null
        );
    }
}
