package com.dirmon.project.auth.service;

import com.dirmon.project.common.exception.JWTNotValidException;
import com.dirmon.project.user.model.UserModel;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public interface JWTService {
    String generateAccessToken(@NonNull UserModel userEntity);
    Claims validateAccessToken(String accessToken) throws JWTNotValidException;

    String generateRefreshToken(@NonNull UserModel userEntity);
    Claims validateRefreshToken(String token) throws JWTNotValidException;

    String extractId(String token) throws JWTNotValidException;
    String extractSubject(String token) throws JWTNotValidException;
    Instant extractExpiration(String token) throws JWTNotValidException;

    @Transactional
    void revokeAllTokensByUser(UserModel userEntity);
    @Transactional
    void revokeTokenByTokenId(UUID tokenId);
}
