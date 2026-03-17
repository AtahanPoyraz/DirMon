package com.dirmon.project.auth.repository;

import com.dirmon.project.auth.model.TokenModel;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<@NonNull TokenModel, @NonNull UUID> {
    @Modifying
    @Query("UPDATE TokenModel t SET t.isRevoked = true WHERE t.user.userId = :userId")
    void revokeAllTokensByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE TokenModel t SET t.isRevoked = true WHERE t.tokenId = :tokenId")
    void revokeTokenById(@Param("tokenId") UUID tokenId);
}