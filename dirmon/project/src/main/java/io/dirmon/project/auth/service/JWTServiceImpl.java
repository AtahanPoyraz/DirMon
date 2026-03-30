package io.dirmon.project.auth.service;

import io.dirmon.project.auth.model.RefreshTokenModel;
import io.dirmon.project.auth.repository.RefreshTokenRepository;
import io.dirmon.project.auth.exception.JWTNotValidException;
import io.dirmon.project.user.model.UserModel;
import io.dirmon.project.util.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
public class JWTServiceImpl implements JWTService {
    @Value("${spring.security.token.auth.secret}")
    private String secret;

    @Value("${spring.security.token.access.expiration}")
    private Long accessExpire;

    @Value("${spring.security.token.refresh.expiration}")
    private Long refreshExpire;

    private SecretKey secretKey;

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JWTServiceImpl(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(this.secret));
    }

    private String createToken(
            String tokenId,
            String subject,
            Map<String, Object> claims,
            Date issuedAt,
            Date expireAt
    ) {
        return Jwts.builder()
                .id(tokenId)
                .subject(subject)
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expireAt)
                .signWith(this.secretKey)
                .compact();
    }

    private Claims parseAndValidateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JWTNotValidException("Token expired");

        } catch (JwtException e) {
            throw new JWTNotValidException("Token invalid");
        }
    }

    @Override
    public String generateAccessToken(
            @NonNull UserModel userEntity
    ) {
        UUID tokenId = UUID.randomUUID();
        UUID userId = userEntity.getUserId();

        List<String> roles = Optional.ofNullable(userEntity.getAuthorities())
                .orElse(Collections.emptyList())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusMillis(this.accessExpire);

        return this.createToken(
                tokenId.toString(),
                userId.toString(),
                Map.of("enabled", userEntity.isEnabled() ,"roles", roles),
                TimeProvider.convertInstantToDate(issuedAt),
                TimeProvider.convertInstantToDate(expiration)
        );
    }

    @Override
    public Claims validateAccessToken(String accessToken) throws JWTNotValidException {
        return this.parseAndValidateToken(accessToken);
    }

    @Override
    public String generateRefreshToken(
            @NonNull UserModel userEntity
    ) {
        UUID tokenId = UUID.randomUUID();
        UUID userId = userEntity.getUserId();

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusMillis(this.refreshExpire);

        RefreshTokenModel tokenEntity = RefreshTokenModel.builder()
                .tokenId(tokenId)
                .user(userEntity)
                .isRevoked(false)
                .signedAt(issuedAt)
                .expiresAt(expiration)
                .build();

        this.refreshTokenRepository.save(tokenEntity);

        return this.createToken(
                tokenId.toString(),
                userId.toString(),
                Collections.emptyMap(),
                TimeProvider.convertInstantToDate(issuedAt),
                TimeProvider.convertInstantToDate(expiration)
        );
    }

    @Override
    public Claims validateRefreshToken(String refreshToken) throws JWTNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(refreshToken);

        UUID tokenId = UUID.fromString(tokenClaims.getId());
        RefreshTokenModel tokenEntity = this.refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new JWTNotValidException("Token not found"));

        if (tokenEntity.isRevoked()) {
            throw new JWTNotValidException("Token is revoked");
        }

        return tokenClaims;
    }

    @Override
    public String extractId(String token) throws JWTNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getId() == null) {
            throw new JWTNotValidException("Token not valid");
        }

        return tokenClaims.getId();
    }

    @Override
    public String extractSubject(String token) throws JWTNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getSubject() == null) {
            throw new JWTNotValidException("Token not valid");
        }

        return tokenClaims.getSubject();
    }

    @Override
    public Instant extractExpiration(String token) throws JWTNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        return TimeProvider.convertDateToInstant(tokenClaims.getExpiration());
    }

    @Override
    @Transactional
    public void revokeAllTokensByUser(UserModel userEntity) {
        this.refreshTokenRepository.revokeAllTokensByUserId(userEntity.getUserId());
    }

    @Override
    @Transactional
    public void revokeTokenByTokenId(UUID tokenId) {
        this.refreshTokenRepository.revokeTokenById(tokenId);
    }
}