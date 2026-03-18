package com.dirmon.project.agent.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentRequest {
    private String name;
    private String description;
}
