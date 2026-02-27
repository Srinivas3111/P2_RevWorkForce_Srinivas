package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String action;

    @Column(columnDefinition = "CLOB")
    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ActivityLog() {
    }

    public ActivityLog(User user, String action, String details) {
        this.user = user;
        this.action = action;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
