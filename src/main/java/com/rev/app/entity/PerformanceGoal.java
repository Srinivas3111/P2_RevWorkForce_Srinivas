package com.rev.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "performance_goals")
public class PerformanceGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_goal_seq")
    @SequenceGenerator(name = "performance_goal_seq", sequenceName = "performance_goal_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    @Column(name = "goal_title", nullable = false, length = 200)
    private String goalTitle;

    @Column(name = "goal_description", length = 2000)
    private String goalDescription;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "goal_priority", length = 10)
    private String goalPriority;

    @Column(name = "completion_percentage")
    private Integer completionPercentage;

    @Column(name = "goal_status", nullable = false, length = 30)
    private String goalStatus;

    @Column(name = "progress_note", length = 2000)
    private String progressNote;

    @Column(name = "manager_comment", length = 2000)
    private String managerComment;

    @Column(name = "final_rating")
    private Integer finalRating;

    @Column(name = "manager_commented_on")
    private LocalDate managerCommentedOn;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @PrePersist
    private void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDate.now();
        }
        if (completionPercentage == null) {
            completionPercentage = 0;
        }
        if (completionPercentage < 0) {
            completionPercentage = 0;
        } else if (completionPercentage > 100) {
            completionPercentage = 100;
        }
        if (goalStatus == null || goalStatus.trim().isEmpty()) {
            goalStatus = "NOT_STARTED";
        }
        if (goalPriority == null || goalPriority.trim().isEmpty()) {
            goalPriority = "MEDIUM";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
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

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(String goalStatus) {
        this.goalStatus = goalStatus;
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

    public Integer getFinalRating() {
        return finalRating;
    }

    public void setFinalRating(Integer finalRating) {
        this.finalRating = finalRating;
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
}
