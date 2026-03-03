package com.rev.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_activity_logs")
public class SystemActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "module_name", nullable = false, length = 80)
    private String moduleName;

    @Column(name = "action_name", nullable = false, length = 120)
    private String actionName;

    @Column(name = "actor_employee_id")
    private Long actorEmployeeId;

    @Column(name = "actor_name", nullable = false, length = 160)
    private String actorName;

    @Column(name = "actor_email", length = 160)
    private String actorEmail;

    @Column(name = "actor_role", length = 40)
    private String actorRole;

    @Column(name = "target_employee_id")
    private Long targetEmployeeId;

    @Column(name = "target_employee_name", length = 160)
    private String targetEmployeeName;

    @Column(name = "details", length = 2000)
    private String details;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    private void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Long getActorEmployeeId() {
        return actorEmployeeId;
    }

    public void setActorEmployeeId(Long actorEmployeeId) {
        this.actorEmployeeId = actorEmployeeId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public void setTargetEmployeeId(Long targetEmployeeId) {
        this.targetEmployeeId = targetEmployeeId;
    }

    public String getTargetEmployeeName() {
        return targetEmployeeName;
    }

    public void setTargetEmployeeName(String targetEmployeeName) {
        this.targetEmployeeName = targetEmployeeName;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
}
