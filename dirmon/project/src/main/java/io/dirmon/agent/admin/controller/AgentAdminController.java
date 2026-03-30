package io.dirmon.agent.admin.controller;

import io.dirmon.agent.admin.dto.AgentResponse;
import io.dirmon.agent.admin.dto.CreateAgentRequest;
import io.dirmon.agent.admin.dto.UpdateAgentRequest;
import io.dirmon.agent.admin.service.AgentAdminService;
import io.dirmon.agent.model.AgentModel;
import io.dirmon.common.dto.GenericResponse;
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
@RequestMapping("/api/v1/agent/admin")
public class AgentAdminController {
    private final AgentAdminService agentAdminService;

    @Autowired
    public AgentAdminController(
            AgentAdminService agentAdminService
    ) {
        this.agentAdminService = agentAdminService;
    }

    @GetMapping
    public ResponseEntity<@NonNull GenericResponse<?>> fetchAgent(
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) UUID userId,
            @ParameterObject Pageable pageable
    ) {
        if (userId != null && agentId != null) {
            AgentModel agentEntity = this.agentAdminService.fetchAgentByUserIdAndAgentId(userId, agentId);
            AgentResponse agentResponse = convertEntityToDto(agentEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentResponse
            );
        }

        if (agentId != null) {
            AgentModel agentEntity = this.agentAdminService.fetchAgentById(agentId);
            AgentResponse agentResponse = convertEntityToDto(agentEntity);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentResponse
            );
        }

        if (userId != null) {
            List<AgentModel> agentEntity = this.agentAdminService.fetchAgentsByUserId(userId);
            List<AgentResponse> agentResponses = agentEntity.stream().map(AgentAdminController::convertEntityToDto).toList();
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agents were fetched successfully",
                    agentResponses
            );
        }

        Page<@NonNull AgentModel> agentEntities = this.agentAdminService.fetchAgents(pageable);
        Page<@NonNull AgentResponse> agentResponses = agentEntities.map(AgentAdminController::convertEntityToDto);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were fetched successfully",
                agentResponses
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createAgent(
            @Valid @RequestBody CreateAgentRequest createAgentRequest
    ) {
        AgentModel agentEntity = this.agentAdminService.createAgent(createAgentRequest);
        AgentResponse agentResponse = convertEntityToDto(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "Agent was created successfully",
                agentResponse
        );
    }

    @PatchMapping
    public ResponseEntity<@NonNull GenericResponse<?>> updateAgent(
            @RequestParam(required = true) UUID agentId,
            @Valid @RequestBody UpdateAgentRequest updateAgentRequest
    ) {
        AgentModel agentEntity = this.agentAdminService.updateAgentByAgentId(agentId, updateAgentRequest);
        AgentResponse agentResponse = convertEntityToDto(agentEntity);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent was updated successfully",
                agentResponse
        );
    }

    @DeleteMapping
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
            this.agentAdminService.deleteAgentsByUserId(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agents were deleted successfully for userId",
                    null
            );
        }

        if (agentId.size() == 1) {
            this.agentAdminService.deleteAgentByAgentId(agentId.getFirst());
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was deleted successfully",
                    null
            );
        }

        this.agentAdminService.deleteAgentsByAgentIds(agentId);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were deleted successfully",
                null
        );
    }

    private static AgentResponse convertEntityToDto(AgentModel agentEntity) {
        return AgentResponse.builder()
                .agentId(agentEntity.getAgentId())
                .userId(agentEntity.getUser().getUserId())
                .name(agentEntity.getName())
                .description(agentEntity.getDescription())
                .status(agentEntity.getStatus())
                .createdAt(agentEntity.getCreatedAt())
                .updatedAt(agentEntity.getUpdatedAt())
                .build();
    }
}
