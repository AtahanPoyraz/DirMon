package io.dirmon.agent.service;

import io.dirmon.agent.exception.AgentTokenNotValidException;
import io.dirmon.agent.model.AgentModel;
import io.dirmon.agent.repository.AgentRepository;
import io.dirmon.agent.exception.AgentNotFoundException;
import io.dirmon.agent.exception.AgentTokenException;
import io.dirmon.util.CryptoProvider;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class AgentTokenServiceImpl implements AgentTokenService {
    @Value("${spring.security.token.agent.secret}")
    private String secret;

    @Value("${spring.security.token.agent.activation.expiration}")
    private Long activationExpire;

    @Value("${spring.security.token.agent.heartbeat.expiration}")
    private Long heartbeatExpire;

    private SecretKeySpec secretKey;

    private final AgentRepository agentRepository;

    @Autowired
    public AgentTokenServiceImpl(
            AgentRepository agentRepository
    ) {
        this.agentRepository = agentRepository;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(this.secret);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    private String generateToken(String subject, Instant issuedAt, Instant expireAt) {
        try {
            String payload = subject + ":" + issuedAt.getEpochSecond() + ":" + expireAt.getEpochSecond();
            byte[] encrypted = CryptoProvider.encrypt(payload.getBytes(StandardCharsets.UTF_8), secretKey);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new AgentTokenException(e.getMessage());
        }
    }

    private String[] parseToken(String token) {
        try {
            byte[] decoded = Base64.getDecoder().decode(token);
            byte[] decrypted = CryptoProvider.decrypt(decoded, secretKey);
            String payload = new String(decrypted, StandardCharsets.UTF_8);
            return payload.split(":");
        } catch (Exception e) {
            throw new AgentTokenException(e.getMessage());
        }
    }

    @Override
    public String generateActivationToken(@NonNull AgentModel agentEntity) {
        UUID agentId = agentEntity.getAgentId();

        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plusMillis(this.activationExpire);

        return this.generateToken(agentId.toString(), issuedAt, expireAt);
    }

    @Override
    public String generateHeartbeatToken(@NonNull AgentModel agentEntity) {
        UUID agentId = agentEntity.getAgentId();

        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plusMillis(this.heartbeatExpire);

        return this.generateToken(agentId.toString(), issuedAt, expireAt);
    }

    @Override
    public AgentModel extractAndVerifyToken(String token) {
        String[] parts = parseToken(token);
        if (parts.length != 3) {
            throw new AgentTokenNotValidException("Invalid token format");
        }

        Instant issuedAt = Instant.ofEpochSecond(Long.parseLong(parts[1]));
        if (Instant.now().isBefore(issuedAt)) {
            throw new AgentTokenNotValidException("Token not yet valid");
        }

        Instant expireAt = Instant.ofEpochSecond(Long.parseLong(parts[2]));
        if (Instant.now().isAfter(expireAt)) {
            throw new AgentTokenNotValidException("Token expired");
        }

        UUID agentId = UUID.fromString(parts[0]);
        return this.agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found"));
    }
}
