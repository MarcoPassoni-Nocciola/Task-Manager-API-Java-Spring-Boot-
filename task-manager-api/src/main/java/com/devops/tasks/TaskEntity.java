package com.devops.tasks;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

/**
 * Represents a stable task entity (PostgreSQL/JPA Entity).
 * Stores task details, current status, and assigned user.
 */
@Entity
public class TaskEntity {

    // Possible states for the task workflow
    public enum Status {
        TODO, IN_PROGRESS, DONE
    }

    @Id
    private UUID id;
    private String title;
    private String description;
    private String assignedUser;
    private Status status;

    public TaskEntity() {
        // Mock task for simulation
        this.id = UUID.fromString("f0e1d2c3-b4a5-6789-0123-456789abcdef"); 
        this.title = "Implement GitLab CI/CD Pipeline";
        this.description = "Set up 7 stages including Checkstyle, SpotBugs, and integration tests.";
        this.assignedUser = "Alice";
        this.status = Status.TODO;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignedUser() { return assignedUser; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }
}


