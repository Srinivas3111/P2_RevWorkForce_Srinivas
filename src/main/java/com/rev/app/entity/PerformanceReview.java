package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "performance_review")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    private Integer year;

    @Column(columnDefinition = "CLOB")
    private String achievements;

    @Column(columnDefinition = "CLOB")
    private String improvements;

    private Integer selfRating; // 1-5

    private Integer managerRating; // 1-5

    @Column(columnDefinition = "CLOB")
    private String managerFeedback;

    private String status; // PENDING, SUBMITTED, REVIEWED, COMPLETED

    public PerformanceReview() {
    }

    public PerformanceReview(Employee employee, Integer year, String achievements,
            String improvements, Integer selfRating, Integer managerRating,
            String managerFeedback, String status) {
        this.employee = employee;
        this.year = year;
        this.achievements = achievements;
        this.improvements = improvements;
        this.selfRating = selfRating;
        this.managerRating = managerRating;
        this.managerFeedback = managerFeedback;
        this.status = status;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getImprovements() {
        return improvements;
    }

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }

    public Integer getSelfRating() {
        return selfRating;
    }

    public void setSelfRating(Integer selfRating) {
        this.selfRating = selfRating;
    }

    public Integer getManagerRating() {
        return managerRating;
    }

    public void setManagerRating(Integer managerRating) {
        this.managerRating = managerRating;
    }

    public String getManagerFeedback() {
        return managerFeedback;
    }

    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
