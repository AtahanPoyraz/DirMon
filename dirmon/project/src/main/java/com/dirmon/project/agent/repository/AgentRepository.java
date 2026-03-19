package com.dirmon.project.agent.repository;

import com.dirmon.project.agent.model.AgentModel;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<@NonNull AgentModel, @NonNull UUID> {
    @NonNull
    List<AgentModel> findAllByUser_UserId(UUID userId);

    @Query("SELECT a FROM AgentModel a WHERE a.user.userId = :userId AND a.agentId = :agentId")
    Optional<AgentModel> findByUserIdAndAgentId(@Param("userId") UUID userId, @Param("agentId") UUID agentId);

    @Modifying
    @Query("""
    UPDATE AgentModel a
    SET a.status = 'STATUS_INACTIVE', a.updatedAt = CURRENT_TIMESTAMP
    WHERE a.status = 'STATUS_ACTIVE' AND a.updatedAt < :thresholdTime
    """)
    int setAgentStatusInactive(@Param("thresholdTime") Instant thresholdTime);

    List<AgentModel> findAllByUser_UserIdAndAgentIdIn(UUID userId, List<UUID> agentIds);
}
