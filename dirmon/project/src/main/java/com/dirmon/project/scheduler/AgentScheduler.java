package com.dirmon.project.scheduler;

import com.dirmon.project.agent.repository.AgentRepository;
import com.dirmon.project.util.ApplicationLogger;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AgentScheduler {
    @Value("${agent.scheduler.offline-threshold.interval}")
    private long offlineThresholdInterval;

    private final AgentRepository agentRepository;

    @Autowired
    public AgentScheduler(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Scheduled(fixedDelayString = "${agent.scheduler.offline-threshold.interval}")
    @Transactional
    public void checkOfflineAgents() {
        try {
            Instant thresholdTime = Instant.now().minusMillis(offlineThresholdInterval);
            int updatedCount = agentRepository.setAgentStatusInactive(thresholdTime);
            if (updatedCount > 0) {
                ApplicationLogger.info(
                        AgentScheduler.class,
                        "Offline agent check completed. Agents updated: " + updatedCount
                );
            }

        } catch (Exception e) {
            ApplicationLogger.warn(
                    AgentScheduler.class,
                    "Offline agent check failed. Exception: " + e.getMessage()
            );
        }
    }
}
