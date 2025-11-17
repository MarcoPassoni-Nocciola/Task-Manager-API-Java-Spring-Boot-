package com.devops.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Basic in-memory service that manages {@link TaskEntity} instances.
 * This mock implementation simulates task lifecycle operations that would
 * eventually persist to a database.
 */
public class TaskService {

    private final List<TaskEntity> tasks = new ArrayList<>();

    public TaskService() {
        // Seed with the default entity from TaskEntity constructor
        tasks.add(new TaskEntity());
    }

    public List<TaskEntity> findAll() {
        return List.copyOf(tasks);
    }

    public TaskEntity findById(UUID id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public TaskEntity create(String title, String description, String assignedUser) {
        TaskEntity task = new TaskEntity();
        task.setAssignedUser(assignedUser);
        task.setStatus(TaskEntity.Status.TODO);
        tasks.add(task);
        return task;
    }

    public TaskEntity updateStatus(UUID id, TaskEntity.Status status) {
        TaskEntity task = findById(id);
        task.setStatus(status);
        return task;
    }
}

