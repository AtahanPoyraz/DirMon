package io.dirmon.agent.controller;

import io.dirmon.agent.dto.AgentResponse;
import io.dirmon.agent.dto.CreateAgentRequest;
import io.dirmon.agent.dto.UpdateAgentConfigRequest;
import io.dirmon.agent.dto.UpdateAgentDetailsRequest;
import io.dirmon.agent.model.AgentModel;
import io.dirmon.agent.service.AgentService;
import io.dirmon.agent.service.AgentTokenService;
import io.dirmon.common.dto.GenericResponse;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {
    private final AgentService agentService;
    private final AgentTokenService agentTokenService;
    private final UserService userService;

    @Autowired
    public AgentController(
            AgentService agentService,
            AgentTokenService agentTokenService,
            UserService userService
    ) {
        this.agentService = agentService;
        this.agentTokenService = agentTokenService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchAgent(
            @RequestParam(required = false) UUID agentId
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        if (agentId != null) {
            AgentModel agentEntity = this.agentService.fetchAgentByUserIdAndAgentId(userEntity.getUserId(), agentId);
            AgentResponse agentResponse = convertEntityToDto(agentEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentResponse
            );
        }

        List<AgentModel> agentEntities = this.agentService.fetchAllAgentsByUserId(userEntity.getUserId());
        List<AgentResponse> agentRespons = agentEntities.stream().map(AgentController::convertEntityToDto).toList();
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were fetched successfully",
                agentRespons
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createAgent(
            @Valid @RequestBody CreateAgentRequest createAgentRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        AgentModel agentEntity = this.agentService.createAgentByUserId(userEntity.getUserId(), createAgentRequest);

        AgentResponse agentResponse = convertEntityToDto(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent was created successfully",
                agentResponse
        );
    }

    @PatchMapping("/details")
    public ResponseEntity<@NonNull GenericResponse<?>> updateAgentDetails(
            @RequestParam(required = true) UUID agentId,
            @Valid @RequestBody UpdateAgentDetailsRequest updateAgentDetailsRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        AgentModel agentEntity = this.agentService.updateAgentDetailsByUserIdAndAgentId(userEntity.getUserId(), agentId, updateAgentDetailsRequest);

        AgentResponse agentResponse = convertEntityToDto(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent details was updated successfully",
                agentResponse
        );
    }

    @PatchMapping("/config")
    public ResponseEntity<@NonNull GenericResponse<?>> updateAgentConfig(
            @RequestParam(required = true) UUID agentId,
            @Valid @RequestBody UpdateAgentConfigRequest updateAgentConfigRequest
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        AgentModel agentEntity = this.agentService.updateAgentConfigByUserIdAndAgentId(userEntity.getUserId(), agentId, updateAgentConfigRequest);

        AgentResponse agentResponse = convertEntityToDto(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent config was updated successfully",
                agentResponse
        );
    }

    @PostMapping("/activation-token")
    public ResponseEntity<@NonNull GenericResponse<?>> createAgentToken(
            @RequestParam(required = true) UUID agentId
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        AgentModel agentEntity = this.agentService.fetchAgentByUserIdAndAgentId(userEntity.getUserId(), agentId);

        String activationToken = this.agentTokenService.generateActivationToken(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Activation token was generated successfully",
                activationToken
        );
    }

    @PostMapping("/activate")
    public ResponseEntity<@NonNull GenericResponse<?>> activate(
            @RequestParam(required = true) String token
    ) {
        AgentModel agentEntity = this.agentTokenService.extractAndVerifyToken(token);
        agentEntity = this.agentService.activateAgentByAgentId(agentEntity.getAgentId());

        String heartbeatToken = this.agentTokenService.generateHeartbeatToken(agentEntity);

        // TASKS, CONFIG and New Heartbeat TOKEN
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent was activated successfully",
                heartbeatToken
        );
    }

    @DeleteMapping
    public ResponseEntity<@NonNull GenericResponse<?>> deleteAgent(
            @RequestParam(required = true) List<UUID> agentId
    ) {
        UserModel userEntity = this.getUserFromSecurityContext();
        if (agentId.isEmpty()) {
            return GenericResponse.genericResponse(
                    HttpStatus.BAD_REQUEST,
                    "No agentIds provided",
                    null
            );
        }

        if (agentId.size() == 1) {
            this.agentService.deleteAgentByUserIdAndAgentId(userEntity.getUserId(), agentId.getFirst());
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was deleted successfully",
                    null
            );
        }

        this.agentService.deleteAllAgentsByUserIdAndAgentIds(userEntity.getUserId(), agentId);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Users were deleted successfully",
                null
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

    private static AgentResponse convertEntityToDto(AgentModel agentEntity) {
        return AgentResponse.builder()
                .agentId(agentEntity.getAgentId())
                .name(agentEntity.getName())
                .description(agentEntity.getDescription())
                .config(agentEntity.getConfig())
                .status(agentEntity.getStatus())
                .createdAt(agentEntity.getCreatedAt())
                .updatedAt(agentEntity.getUpdatedAt())
                .build();
    }
}
