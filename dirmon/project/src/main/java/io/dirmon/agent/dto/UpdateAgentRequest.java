package io.dirmon.agent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentRequest {
    @NotBlank(message = "Agent name cannot be empty")
    private String name;
    private String description;

    @Valid
    private AgentConfigDto config;

    @Getter
    @Setter
    private static class AgentConfigDto {
        @NotNull(message = "Heartbeat interval cannot be null")
        @Min(value = 3, message = "Heartbeat interval must be at least 3")
        @Max(value = 300, message = "Heartbeat interval must be at most 300")
        private Integer heartbeatIntervalSeconds;
    }
}
