package io.dirmon.project.user.scheduler;

import io.dirmon.project.common.logging.ApplicationLogger;
import io.dirmon.project.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserScheduler {
    @Value("${user.scheduler.deletion-threshold.interval}")
    private long userDeletionThreshold;

    private final UserRepository userRepository;

    @Autowired
    public UserScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteInactiveUsers() {
        try {
            Instant thresholdTime = Instant.now().minusMillis(userDeletionThreshold);

            int deletedCount = this.userRepository.deleteByEnabledFalseAndLastLoginBefore(thresholdTime);
            if (deletedCount > 0) {
                ApplicationLogger.info(
                        UserScheduler.class,
                        "Inactive users deleted: " + deletedCount
                );
            }

        } catch (Exception e) {
            ApplicationLogger.warn(
                    UserScheduler.class,
                    "User cleanup failed: " + e.getMessage()
            );
        }
    }
}
