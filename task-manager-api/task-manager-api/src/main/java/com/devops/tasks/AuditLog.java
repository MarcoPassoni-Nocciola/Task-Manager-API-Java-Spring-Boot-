package com.devops.tasks;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a dynamic audit log entry (MongoDB Document).
 * Used for high-volume tracking of state changes or user interactions.
 */
@Document(collection = "auditlogs")
public class AuditLog {

    @Id
    private String id;
    private UUID taskId;
    private String eventType; // e.g., "STATUS_CHANGE", "USER_ASSIGNED"
    private String oldValue;
    private String newValue;
    private String performedBy;
    private LocalDateTime timestamp;

    public AuditLog(UUID taskId, String eventType, String oldValue, String newValue, String performedBy) {
        this.taskId = taskId;
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public UUID getTaskId() { return taskId; }
    public String getEventType() { return eventType; }
    public String getNewValue() { return newValue; }
}


