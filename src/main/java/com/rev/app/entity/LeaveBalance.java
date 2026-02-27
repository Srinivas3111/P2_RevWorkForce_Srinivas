package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balance")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer balanceId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private Integer balanceDays;

    private Integer totalDays;

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public LeaveBalance() {
    }

    public LeaveBalance(Employee employee, LeaveType leaveType, Integer balanceDays) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.balanceDays = balanceDays;
        this.totalDays = balanceDays; // Default total to balance when first assigned
    }

    public LeaveBalance(Employee employee, LeaveType leaveType, Integer balanceDays, Integer totalDays) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.balanceDays = balanceDays;
        this.totalDays = totalDays;
    }

    public Integer getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
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

    public Integer getBalanceDays() {
        return balanceDays;
    }

    public void setBalanceDays(Integer balanceDays) {
        this.balanceDays = balanceDays;
    }
}
