package io.dirmon.auth.service;

import io.dirmon.user.model.UserModel;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public interface JWTService {
    String generateAccessToken(@NonNull UserModel userEntity);
    Claims validateAccessToken(String accessToken);

    String generateRefreshToken(@NonNull UserModel userEntity);
    Claims validateRefreshToken(String token);

    String extractId(String token);
    String extractSubject(String token);
    Instant extractExpiration(String token);

    @Transactional
    void revokeAllTokensByUser(UserModel userEntity);
    @Transactional
    void revokeTokenByTokenId(UUID tokenId);
}
