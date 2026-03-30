package io.dirmon.project.agent.service;

import io.dirmon.project.agent.exception.TaskNotFoundException;
import io.dirmon.project.agent.model.TaskModel;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskModel fetchTaskById(UUID taskId) throws TaskNotFoundException;
    List<@NonNull TaskModel> fetchAllTasksByUserId(UUID userId);
}
