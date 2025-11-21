package com.devops.tasks;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for managing Task entities.
 * Uses an in-memory map for simplicity and compatibility
 */
@Service
public class TaskService {

    // In-memory storage for tasks
    private final ConcurrentHashMap<String, Task> taskRepository = new ConcurrentHashMap<>();

    // Initial dummy data for API testing and demonstration
    public TaskService() {
        // Task 1: Assigned to userB, In Progress
        Task t1 = new Task("Implement CI/CD Build Stage", "Set up Maven compile job in the pipeline.", "userA");
        t1.setAssignedToUserId("userB");
        t1.setStatus(Task.Status.IN_PROGRESS);
        taskRepository.put(t1.getId(), t1);

        // Task 2: Assigned to userA, To Do
        Task t2 = new Task("Write Report Introduction", "Draft the first section of the assignment report.", "userB");
        t2.setAssignedToUserId("userA");
        t2.setStatus(Task.Status.TODO);
        taskRepository.put(t2.getId(), t2);
        
        // Task 3: Assigned to userC, Done
        Task t3 = new Task("Define Deployment Strategy", "Plan the final deploy stage to a staging environment.", "userC");
        t3.setAssignedToUserId("userC");
        t3.setStatus(Task.Status.DONE);
        taskRepository.put(t3.getId(), t3);
    }

    /**
     * Returns a snapshot list of all tasks currently stored in the in-memory repository.
     *
     * @return an {@link ArrayList} containing all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskRepository.values());
    }

    /**
     * Returns the task with the given id, if present.
     *
     * @param id the task id
     * @return an {@link Optional} containing the task if found, otherwise {@link Optional#empty()}
     */
    public Optional<Task> getTaskById(String id) {
        return Optional.ofNullable(taskRepository.get(id));
    }

    /**
     * Creates a new task and assigns metadata (id and creator).
     *
     * @param task the task to create
     * @param createdByUserId the id of the user creating the task
     * @return the created {@link Task} instance
     */
    public Task createTask(Task task, String createdByUserId) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedByUserId(createdByUserId);
        if (task.getAssignedToUserId() == null) {
             task.setAssignedToUserId(createdByUserId);
        }
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.TODO);
        }
        taskRepository.put(task.getId(), task);
        return task;
    }

    /**
     * Updates an existing task with the provided values.
     *
     * @param id the id of the task to update
     * @param updatedTask the task object containing updated values
     * @return an {@link Optional} containing the updated task if the id was found, otherwise {@link Optional#empty()}
     */
    public Optional<Task> updateTask(String id, Task updatedTask) {
        Task existingTask = taskRepository.get(id);
        if (existingTask != null) {
            existingTask.setTitle(updatedTask.getTitle() != null ? updatedTask.getTitle() : existingTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription() != null ? updatedTask.getDescription() : existingTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus() != null ? updatedTask.getStatus() : existingTask.getStatus());
            existingTask.setAssignedToUserId(updatedTask.getAssignedToUserId() != null ? updatedTask.getAssignedToUserId() : existingTask.getAssignedToUserId());
            
            taskRepository.put(id, existingTask);
            return Optional.of(existingTask);
        }
        return Optional.empty();
    }

    /**
     * Deletes the task with the specified id.
     *
     * @param id the task id
     * @return {@code true} if the task was deleted, {@code false} otherwise
     */
    public boolean deleteTask(String id) {
        return taskRepository.remove(id) != null;
    }
    
    /**
     * Returns the tasks assigned to the specified user.
     *
     * @param userId the id of the assigned user
     * @return a list of tasks assigned to the user
     */
    public List<Task> getTasksAssignedToUser(String userId) {
        return taskRepository.values().stream()
                .filter(task -> userId.equals(task.getAssignedToUserId()))
                .toList();
    }
}


