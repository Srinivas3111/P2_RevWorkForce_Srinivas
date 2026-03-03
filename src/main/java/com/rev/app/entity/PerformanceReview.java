package com.rev.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "employee_id", "review_period" })
})
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_review_seq")
    @SequenceGenerator(name = "performance_review_seq", sequenceName = "performance_review_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    @Column(name = "review_period", nullable = false, length = 40)
    private String reviewPeriod;

    @Column(name = "self_assessment", nullable = false, length = 3000)
    private String selfAssessment;

    @Column(name = "achievements", length = 2000)
    private String achievements;

    @Column(name = "challenges", length = 2000)
    private String challenges;

    @Column(name = "self_rating")
    private Integer selfRating;

    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "manager_feedback", length = 4000)
    private String managerFeedback;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "submitted_on", nullable = false)
    private LocalDate submittedOn;

    @Column(name = "reviewed_on")
    private LocalDate reviewedOn;

    @PrePersist
    private void onCreate() {
        if (submittedOn == null) {
            submittedOn = LocalDate.now();
        }
        if (status == null || status.trim().isEmpty()) {
            status = "SUBMITTED";
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

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public String getSelfAssessment() {
        return selfAssessment;
    }

    public void setSelfAssessment(String selfAssessment) {
        this.selfAssessment = selfAssessment;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getChallenges() {
        return challenges;
    }

    public void setChallenges(String challenges) {
        this.challenges = challenges;
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

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDate submittedOn) {
        this.submittedOn = submittedOn;
    }

    public LocalDate getReviewedOn() {
        return reviewedOn;
    }

    public void setReviewedOn(LocalDate reviewedOn) {
        this.reviewedOn = reviewedOn;
    }
}
