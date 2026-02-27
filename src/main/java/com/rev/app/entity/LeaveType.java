package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveTypeId;

    private String leaveName;

    @Column(columnDefinition = "CLOB")
    private String description;

    private Integer maxDaysPerYear;
    private String applicableGender; // MALE, FEMALE, ALL

    public LeaveType() {
    }

    public LeaveType(String leaveName, String description, Integer maxDaysPerYear, String applicableGender) {
        this.leaveName = leaveName;
        this.description = description;
        this.maxDaysPerYear = maxDaysPerYear;
        this.applicableGender = applicableGender;
    }

    public String getApplicableGender() {
        return applicableGender;
    }

    public void setApplicableGender(String applicableGender) {
        this.applicableGender = applicableGender;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveName() {
        return leaveName;
    }

    public void setLeaveName(String leaveName) {
        this.leaveName = leaveName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxDaysPerYear() {
        return maxDaysPerYear;
    }

    public void setMaxDaysPerYear(Integer maxDaysPerYear) {
        this.maxDaysPerYear = maxDaysPerYear;
    }
}
