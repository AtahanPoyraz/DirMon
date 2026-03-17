package com.dirmon.project.auth.service;

import com.dirmon.project.common.exception.TokenNotValidException;
import com.dirmon.project.user.model.UserModel;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public interface TokenService {
    String generateAccessToken(@NonNull UserModel userEntity);
    Claims validateAccessToken(String accessToken) throws TokenNotValidException;

    String generateRefreshToken(@NonNull UserModel userEntity);
    Claims validateRefreshToken(String token) throws TokenNotValidException;

    String extractId(String token) throws TokenNotValidException;
    String extractSubject(String token) throws TokenNotValidException;
    Instant extractExpiration(String token) throws TokenNotValidException;

    @Transactional
    void revokeAllTokensByUser(UserModel userEntity);
    @Transactional
    void revokeTokenByTokenId(UUID tokenId);
}
