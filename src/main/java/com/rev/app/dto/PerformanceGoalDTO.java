package com.rev.app.dto;

import java.time.LocalDate;

public class PerformanceGoalDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long managerId;
    private String goalTitle;
    private String goalDescription;
    private LocalDate targetDate;
    private String goalPriority;
    private String goalStatus;
    private Integer completionPercentage;
    private String progressNote;
    private String managerComment;
    private Integer finalRating;
    private LocalDate managerCommentedOn;
    private LocalDate createdOn;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getGoalPriority() {
        return goalPriority;
    }

    public void setGoalPriority(String goalPriority) {
        this.goalPriority = goalPriority;
    }

    public String getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(String goalStatus) {
        this.goalStatus = goalStatus;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getProgressNote() {
        return progressNote;
    }

    public void setProgressNote(String progressNote) {
        this.progressNote = progressNote;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public LocalDate getManagerCommentedOn() {
        return managerCommentedOn;
    }

    public void setManagerCommentedOn(LocalDate managerCommentedOn) {
        this.managerCommentedOn = managerCommentedOn;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getFinalRating() {
        return finalRating;
    }

    public void setFinalRating(Integer finalRating) {
        this.finalRating = finalRating;
    }
}
