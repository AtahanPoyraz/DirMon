package io.dirmon.project.agent.model;

import io.dirmon.project.user.model.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tasks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_agent_task_name",
                        columnNames = {"agent_id", "name"}
                )
        }
)
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_id", unique = true, nullable = false, updatable = false)
    private UUID taskId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id", nullable = true)
    private AgentModel assignedAgent;

    @Column(name = "name", unique = false, nullable = false, updatable = true)
    private String name;

    @Column(name = "description", unique = false, nullable = true, updatable = true, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", unique = false, nullable = false, updatable = true)
    private TaskStatus status;

    @Builder.Default
    @OneToMany(
            mappedBy = "task",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DirectoryModel> directories = new ArrayList<>();

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
