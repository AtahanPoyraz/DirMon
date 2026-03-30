package io.dirmon.auth.repository;

import io.dirmon.auth.model.RefreshTokenModel;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<@NonNull RefreshTokenModel, @NonNull UUID> {
    @Modifying
    @Query("UPDATE RefreshTokenModel t SET t.isRevoked = true WHERE t.user.userId = :userId")
    void revokeAllTokensByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE RefreshTokenModel t SET t.isRevoked = true WHERE t.tokenId = :tokenId")
    void revokeTokenById(@Param("tokenId") UUID tokenId);
}