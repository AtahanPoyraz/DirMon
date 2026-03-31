package io.dirmon.agent.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentConfigRequest {
    @Min(value = 3, message = "Heartbeat interval must be at least 3 seconds")
    @Max(value = 300, message = "Heartbeat interval must be at most 300 seconds")
    private Integer heartbeatIntervalSeconds;
}
