package io.dirmon.project.agent.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatResponse {
    private String token;
}
