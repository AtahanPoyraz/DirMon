package com.dirmon.project.admin.controller;

import com.dirmon.project.admin.dto.agent.CreateAgentRequest;
import com.dirmon.project.admin.dto.agent.UpdateAgentRequest;
import com.dirmon.project.admin.service.AgentAdminService;
import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.common.dto.GenericResponse;
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
@RequestMapping("/api/v1/admin/agent")
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
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentEntity
            );
        }

        if (agentId != null) {
            AgentModel agentEntity = this.agentAdminService.fetchAgentById(agentId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agent was fetched successfully",
                    agentEntity
            );
        }

        if (userId != null) {
            List<AgentModel> agentEntity = this.agentAdminService.fetchAgentsByUserId(userId);
            return GenericResponse.genericResponse(
                    HttpStatus.OK,
                    "Agents were fetched successfully",
                    agentEntity
            );
        }

        Page<@NonNull AgentModel> agentEntities = this.agentAdminService.fetchAgents(pageable);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agents were fetched successfully",
                agentEntities
        );
    }

    @PostMapping
    public ResponseEntity<@NonNull GenericResponse<?>> createAgent(
            @Valid @RequestBody CreateAgentRequest createAgentRequest
    ) {
        AgentModel agentEntity = this.agentAdminService.createAgent(createAgentRequest);
        return GenericResponse.genericResponse(
                HttpStatus.CREATED,
                "Agent was created successfully",
                agentEntity
        );
    }

    @PatchMapping
    public ResponseEntity<@NonNull GenericResponse<?>> updateAgent(
            @RequestParam(required = true) UUID agentId,
            @Valid @RequestBody UpdateAgentRequest updateAgentRequest
    ) {
        AgentModel agentEntity = this.agentAdminService.updateAgentByAgentId(agentId, updateAgentRequest);
        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Agent was updated successfully",
                agentEntity
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
}
