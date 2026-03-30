package io.dirmon.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "directories")
public class DirectoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "directory_id", unique = true, nullable = false, updatable = false)
    private UUID directoryId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskModel task;

    @Column(name = "path", unique = false, nullable = false, updatable = true)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
