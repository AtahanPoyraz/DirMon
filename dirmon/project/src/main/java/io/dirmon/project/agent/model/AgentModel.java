package io.dirmon.project.agent.model;

import io.dirmon.project.user.model.UserModel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "name", unique = false, nullable = false, updatable = true)
    private String name;

    @Column(name = "description", unique = false, nullable = true, updatable = true, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", unique = false, nullable = false, updatable = true)
    private AgentStatus status;

    @OneToOne(mappedBy = "assignedAgent")
    private TaskModel task;

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
