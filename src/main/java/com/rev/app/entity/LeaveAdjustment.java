package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_adjustments")
public class LeaveAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adjustmentId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private Integer adjustmentAmount;

    @Column(columnDefinition = "CLOB")
    private String reason;

    private LocalDateTime adjustedAt;

    public LeaveAdjustment() {
    }

    public LeaveAdjustment(Employee employee, LeaveType leaveType, Integer adjustmentAmount, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.adjustmentAmount = adjustmentAmount;
        this.reason = reason;
        this.adjustedAt = LocalDateTime.now();
    }

    public Integer getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Integer adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getAdjustmentAmount() {
        return adjustmentAmount;
    }

    public void setAdjustmentAmount(Integer adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAdjustedAt() {
        return adjustedAt;
    }

    public void setAdjustedAt(LocalDateTime adjustedAt) {
        this.adjustedAt = adjustedAt;
    }
}
