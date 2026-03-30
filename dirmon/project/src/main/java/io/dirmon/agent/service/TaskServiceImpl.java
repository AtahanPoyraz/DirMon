package io.dirmon.agent.service;

import io.dirmon.agent.exception.TaskNotFoundException;
import io.dirmon.agent.model.TaskModel;
import io.dirmon.agent.repository.DirectoryRepository;
import io.dirmon.agent.repository.TaskRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final DirectoryRepository directoryRepository;

    @Autowired
    public TaskServiceImpl(
            TaskRepository taskRepository,
            DirectoryRepository directoryRepository
    ) {
        this.taskRepository = taskRepository;
        this.directoryRepository = directoryRepository;
    }

    @Override
    public TaskModel fetchTaskById(UUID taskId) {
        return this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
    }

    @Override
    public List<@NonNull TaskModel> fetchAllTasksByUserId(UUID userId) {
        return this.taskRepository.findByUserId(userId);
    }
}
