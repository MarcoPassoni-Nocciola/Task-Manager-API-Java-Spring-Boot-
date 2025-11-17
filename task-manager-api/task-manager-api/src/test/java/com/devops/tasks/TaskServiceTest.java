package com.devops.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void getAllTasksReturnsSeedData() {
        List<Task> tasks = taskService.getAllTasks();

        assertFalse(tasks.isEmpty(), "Seed data should be present");
    }

    @Test
    void createTaskAssignsIdAndDefaults() {
        Task newTask = new Task("Write docs", "Document CI pipeline", "userX");

        Task created = taskService.createTask(newTask, "creator-user");

        assertNotNull(created.getId(), "New task must have an id");
        assertEquals(Task.Status.TODO, created.getStatus(), "Default status should be TODO");
        assertEquals("creator-user", created.getAssignedToUserId(), "Assignee defaults to creator when missing");
        assertEquals("creator-user", created.getCreatedByUserId(), "Creator id must be stored");
    }

    @Test
    void updateTaskChangesMutableFields() {
        Task base = taskService.createTask(new Task("Base", "Base desc", "userA"), "userA");

        Task update = new Task(null, null, null);
        update.setStatus(Task.Status.DONE);
        update.setAssignedToUserId("userB");
        update.setTitle("Updated Title");
        update.setDescription("Updated Description");

        Optional<Task> updated = taskService.updateTask(base.getId(), update);

        assertTrue(updated.isPresent(), "Existing task should be updatable");
        assertEquals(Task.Status.DONE, updated.get().getStatus());
        assertEquals("userB", updated.get().getAssignedToUserId());
        assertEquals("Updated Title", updated.get().getTitle());
        assertEquals("Updated Description", updated.get().getDescription());
    }

    @Test
    void deleteTaskRemovesEntry() {
        Task task = taskService.createTask(new Task("Temp", "To delete", "userTemp"), "userTemp");

        boolean deleted = taskService.deleteTask(task.getId());

        assertTrue(deleted, "Delete should return true for existing tasks");
        assertTrue(taskService.getTaskById(task.getId()).isEmpty(), "Task should be removed");
    }
}

