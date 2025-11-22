package com.devops.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Task management operations.
 * Base path: /api/v1/tasks
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // --- GET methods ---

    /**
     * Retrieve all tasks or tasks assigned to a specific user (via query param).
     * Example: GET /api/v1/tasks?assignedTo=userA
     */
    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) String assignedTo) {
        if (assignedTo != null && !assignedTo.isEmpty()) {
            return taskService.getTasksAssignedToUser(assignedTo);
        }
        return taskService.getAllTasks();
    }

    /**
     * Retrieve a specific task by ID.
     * Example: GET /api/v1/tasks/{taskId}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- POST method ---

    /**
     * Create a new task.
     * Example: POST /api/v1/tasks
     * Body: { "title": "New Task", "description": "Details", "assignedToUserId": "userB" }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        // Hardcode the creating user ID for pipeline testing purposes
        String currentUserId = "current_user"; 
        return taskService.createTask(task, currentUserId);
    }

    // --- PUT method ---

    /**
     * Update an existing task (e.g., changing status or assigned user).
     * Example: PUT /api/v1/tasks/{taskId}
     * Body: { "status": "DONE" }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task taskDetails) {
        Optional<Task> updatedTask = taskService.updateTask(id, taskDetails);
        return updatedTask.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- DELETE method ---

    /**
     * Delete a task by ID.
     * Example: DELETE /api/v1/tasks/{taskId}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


        // --- DELETE method ---

    /**
     * Delete all tasks assigned to a specific user.
     * Example: DELETE /api/v1/tasks/users/{userId}
     * 
     * @param userId The ID of the user whose assigned tasks should be deleted
     * @return 204 No Content if tasks were deleted, 404 Not Found if no tasks were found for the user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteTasksByUserId(@PathVariable String userId) {
        if(taskService.deleteTasksOfUser(userId)){
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
