package io.dirmon.agent.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfig {
    @Builder.Default
    private Integer heartbeatIntervalSeconds = 3;
}
