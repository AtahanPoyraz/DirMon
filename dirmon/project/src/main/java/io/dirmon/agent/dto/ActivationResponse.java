package io.dirmon.agent.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationResponse {
    private String heartbeatToken;
}
