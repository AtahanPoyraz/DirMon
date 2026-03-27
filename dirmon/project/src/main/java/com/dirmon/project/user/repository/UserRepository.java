package com.dirmon.project.user.repository;

import com.dirmon.project.user.model.UserModel;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<@NonNull UserModel, @NonNull UUID> {
    @NonNull
    Page<@NonNull UserModel> findAll(@NonNull Pageable pageable);
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserModel u SET u.lastLogin = :lastLogin WHERE u.userId = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") Instant lastLogin);

    int deleteByEnabledFalseAndLastLoginBefore(Instant thresholdTime);
}
