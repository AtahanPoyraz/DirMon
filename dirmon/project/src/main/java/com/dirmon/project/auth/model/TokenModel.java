package com.dirmon.project.auth.model;

import com.dirmon.project.user.model.UserModel;
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
@Table(name = "tokens")
public class TokenModel {
    @Builder.Default
    @Id
    @Column(name = "token_id", nullable = false, updatable = false)
    private UUID tokenId = UUID.randomUUID();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = false, nullable = false, updatable = false)
    private UserModel user;

    @Builder.Default
    @Column(name = "is_revoked", unique = false, nullable = false)
    private boolean isRevoked = false;

    @Column(name = "signed_at", unique = false, nullable = false, updatable = false)
    private Instant signedAt;

    @Column(name = "expires_at", unique = false, nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "updated_at", unique = false, nullable = false, updatable = true)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (signedAt == null) {
            signedAt = Instant.now();
        }

        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "TokenModel{" +
                "tokenId=" + tokenId +
                ", isRevoked=" + isRevoked +
                ", signedAt=" + signedAt +
                ", expiresAt=" + expiresAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}