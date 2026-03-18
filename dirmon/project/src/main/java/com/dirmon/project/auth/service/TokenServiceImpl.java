package com.dirmon.project.auth.service;

import com.dirmon.project.auth.model.TokenModel;
import com.dirmon.project.auth.repository.TokenRepository;
import com.dirmon.project.common.exception.TokenNotValidException;
import com.dirmon.project.user.model.UserModel;
import com.dirmon.project.util.TimeProvider;
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
public class TokenServiceImpl implements TokenService {
    @Value("${spring.security.token.secret}")
    private String secret;

    @Value("${spring.security.token.access.expiration}")
    private Long accessExpire;

    @Value("${spring.security.token.refresh.expiration}")
    private Long refreshExpire;

    private SecretKey secretKey;

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(
            TokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
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
            Date expiration
    ) {
        return Jwts.builder()
                .id(tokenId)
                .subject(subject)
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
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
            throw new TokenNotValidException("Token expired");

        } catch (JwtException e) {
            throw new TokenNotValidException("Token invalid");
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

        return createToken(
                tokenId.toString(),
                userId.toString(),
                Map.of("enabled", userEntity.isEnabled() ,"roles", roles),
                TimeProvider.convertInstantToDate(issuedAt),
                TimeProvider.convertInstantToDate(expiration)
        );
    }

    @Override
    public Claims validateAccessToken(String accessToken) throws TokenNotValidException {
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

        TokenModel tokenEntity = TokenModel.builder()
                .tokenId(tokenId)
                .user(userEntity)
                .isRevoked(false)
                .signedAt(issuedAt)
                .expiresAt(expiration)
                .build();

        this.tokenRepository.save(tokenEntity);

        return this.createToken(
                tokenId.toString(),
                userId.toString(),
                Collections.emptyMap(),
                TimeProvider.convertInstantToDate(issuedAt),
                TimeProvider.convertInstantToDate(expiration)
        );
    }

    @Override
    public Claims validateRefreshToken(String refreshToken) throws TokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(refreshToken);

        UUID tokenId = UUID.fromString(tokenClaims.getId());
        TokenModel tokenEntity = this.tokenRepository.findById(tokenId)
                .orElseThrow(() -> new TokenNotValidException("Token not found"));

        if (tokenEntity.isRevoked()) {
            throw new TokenNotValidException("Token is revoked");
        }

        return tokenClaims;
    }

    @Override
    public String extractId(String token) throws TokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getId() == null) {
            throw new TokenNotValidException("Token not valid");
        }

        return tokenClaims.getId();
    }

    @Override
    public String extractSubject(String token) throws TokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getSubject() == null) {
            throw new TokenNotValidException("Token not valid");
        }

        return tokenClaims.getSubject();
    }


    @Override
    public Instant extractExpiration(String token) throws TokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        return TimeProvider.convertDateToInstant(tokenClaims.getExpiration());
    }

    @Override
    @Transactional
    public void revokeAllTokensByUser(UserModel userEntity) {
        this.tokenRepository.revokeAllTokensByUserId(userEntity.getUserId());
    }

    @Override
    @Transactional
    public void revokeTokenByTokenId(UUID tokenId) {
        this.tokenRepository.revokeTokenById(tokenId);
    }
}