package io.dirmon.agent.service;

import io.dirmon.agent.exception.TaskNotFoundException;
import io.dirmon.agent.model.TaskModel;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskModel fetchTaskById(UUID taskId) throws TaskNotFoundException;
    List<@NonNull TaskModel> fetchAllTasksByUserId(UUID userId);
}
