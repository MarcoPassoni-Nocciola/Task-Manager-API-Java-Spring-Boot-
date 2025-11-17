package com.devops.tasks;

import java.util.UUID;

/**
 * Simple in-memory task representation used by {@link TaskService}.
 */
public class Task {

    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE
    }

    private String id;
    private String title;
    private String description;
    private String createdByUserId;
    private String assignedToUserId;
    private Status status;

    public Task() {
        this.id = UUID.randomUUID().toString();
    }

    public Task(String title, String description, String createdByUserId) {
        this();
        this.title = title;
        this.description = description;
        this.createdByUserId = createdByUserId;
        this.status = Status.TODO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

