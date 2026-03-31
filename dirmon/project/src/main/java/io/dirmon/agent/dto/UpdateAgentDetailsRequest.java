package io.dirmon.agent.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentDetailsRequest {
    private String name;
    private String description;
}
