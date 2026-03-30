package com.dirmon.project.user.model;

import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.agent.model.TaskModel;
import com.dirmon.project.auth.model.RefreshTokenModel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "first_name", unique = false, nullable = false, updatable = true)
    private String firstName;

    @Column(name = "last_name", unique = false, nullable = false, updatable = true)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, updatable = true)
    private String email;

    @Column(name = "password", unique = false, nullable = false, updatable = true)
    private String password;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles", unique = false, nullable = false, updatable = true)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @Builder.Default
    @Column(name = "enabled", unique = false, nullable = false, updatable = true)
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "account_non_expired", unique = false, nullable = false, updatable = true)
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "account_non_locked", unique = false, nullable = false, updatable = true)
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "credentials_non_expired", unique = false, nullable = false, updatable = true)
    private boolean credentialsNonExpired = true;

    @Column(name = "last_login", unique = false, nullable = true, updatable = true)
    private Instant lastLogin;

    @Column(name = "created_at", unique = false, nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", unique = false, nullable = false, updatable = true)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RefreshTokenModel> refreshTokens = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AgentModel> agents = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TaskModel> tasks = new ArrayList<>();

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    @NonNull
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.lastLogin = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", lastLogin=" + lastLogin +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
