package com.dirmon.project.agent.model;

import com.dirmon.project.user.model.UserModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "agents",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_agent_name",
                        columnNames = {"user_id", "name"}
                )
        }
)
public class AgentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "agent_id", unique = true, nullable = false, updatable = false)
    private UUID agentId;

    @Column(name = "name", unique = false, nullable = false, updatable = true)
    private String name;

    @Column(name = "description", unique = false, nullable = true, updatable = true, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", unique = false, nullable = false, updatable = true)
    private AgentStatus status;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
