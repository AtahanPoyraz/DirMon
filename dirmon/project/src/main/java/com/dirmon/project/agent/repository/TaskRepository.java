package com.dirmon.project.agent.repository;

import com.dirmon.project.agent.model.TaskModel;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<@NonNull TaskModel, @NonNull UUID> {
    @Query("SELECT t FROM TaskModel t WHERE t.user.userId = :userId")
    List<TaskModel> findByUserId(@Param("userId") UUID userId);
}
