package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goalId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(columnDefinition = "CLOB")
    private String goalDesc;

    private LocalDate deadline;

    private String priority; // HIGH, MEDIUM, LOW

    private String successMetric;

    private Integer progress = 0; // 0-100

    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(columnDefinition = "CLOB")
    private String managerComment;

    public Goal() {
    }

    public Goal(Employee employee, String goalDesc, LocalDate deadline,
            String priority, String successMetric, Integer progress, String status) {
        this.employee = employee;
        this.goalDesc = goalDesc;
        this.deadline = deadline;
        this.priority = priority;
        this.successMetric = successMetric;
        this.progress = progress;
        this.status = status;
    }

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getGoalDesc() {
        return goalDesc;
    }

    public void setGoalDesc(String goalDesc) {
        this.goalDesc = goalDesc;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSuccessMetric() {
        return successMetric;
    }

    public void setSuccessMetric(String successMetric) {
        this.successMetric = successMetric;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
}
