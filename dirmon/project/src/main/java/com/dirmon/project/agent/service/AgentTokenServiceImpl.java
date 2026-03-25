package com.dirmon.project.agent.service;

import com.dirmon.project.agent.model.AgentModel;
import com.dirmon.project.agent.repository.AgentRepository;
import com.dirmon.project.common.exception.AgentTokenNotValidException;
import com.dirmon.project.util.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
public class AgentTokenServiceImpl implements AgentTokenService {
    @Value("${spring.security.token.agent.secret}")
    private String secret;

    @Value("${spring.security.token.agent.expiration}")
    private Long expire;

    private SecretKey secretKey;

    private final AgentRepository agentRepository;

    @Autowired
    public AgentTokenServiceImpl(
            AgentRepository agentRepository
    ) {
        this.agentRepository = agentRepository;
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
            throw new AgentTokenNotValidException("Token expired");

        } catch (JwtException e) {
            throw new AgentTokenNotValidException("Token invalid");
        }
    }

    @Override
    public String generateAgentToken(@NonNull AgentModel agentEntity) {
        UUID tokenId = UUID.randomUUID();
        UUID agentId = agentEntity.getAgentId();

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusMillis(this.expire);

        return this.createToken(
                tokenId.toString(),
                agentId.toString(),
                Collections.emptyMap(),
                TimeProvider.convertInstantToDate(issuedAt),
                TimeProvider.convertInstantToDate(expiration)
        );
    }

    @Override
    public Claims validateAgentToken(String agentToken) throws AgentTokenNotValidException {
        return this.parseAndValidateToken(agentToken);
    }

    @Override
    public String extractId(String token) throws AgentTokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getId() == null) {
            throw new AgentTokenNotValidException("Token not valid");
        }

        return tokenClaims.getId();
    }

    @Override
    public String extractSubject(String token) throws AgentTokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        if (tokenClaims.getSubject() == null) {
            throw new AgentTokenNotValidException("Token not valid");
        }

        return tokenClaims.getSubject();
    }

    @Override
    public Instant extractExpiration(String token) throws AgentTokenNotValidException {
        Claims tokenClaims = this.parseAndValidateToken(token);
        return TimeProvider.convertDateToInstant(tokenClaims.getExpiration());
    }
}
