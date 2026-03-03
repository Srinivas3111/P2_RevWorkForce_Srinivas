package com.rev.app.dto;

import java.util.List;

public class ManagerTeamLeaveBalanceSummary {
    private Long employeeId;
    private String employeeName;
    private Integer year;
    private Integer totalAllowedLeaves;
    private Integer usedLeaves;
    private Integer remainingLeaves;
    private List<EmployeeLeaveBalanceSummary> details;

    public ManagerTeamLeaveBalanceSummary(Long employeeId,
            String employeeName,
            Integer year,
            Integer totalAllowedLeaves,
            Integer usedLeaves,
            Integer remainingLeaves) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.year = year;
        this.totalAllowedLeaves = totalAllowedLeaves;
        this.usedLeaves = usedLeaves;
        this.remainingLeaves = remainingLeaves;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getTotalAllowedLeaves() {
        return totalAllowedLeaves;
    }

    public Integer getUsedLeaves() {
        return usedLeaves;
    }

    public Integer getRemainingLeaves() {
        return remainingLeaves;
    }

    public List<EmployeeLeaveBalanceSummary> getDetails() {
        return details;
    }

    public void setDetails(List<EmployeeLeaveBalanceSummary> details) {
        this.details = details;
    }
}
