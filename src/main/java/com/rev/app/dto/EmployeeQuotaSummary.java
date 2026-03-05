package com.rev.app.dto;

public class EmployeeQuotaSummary {
    private Long employeeId;
    private String employeeName;
    private Integer year;
    private Integer totalDays;

    public EmployeeQuotaSummary(Long employeeId, String employeeName, Integer year, Integer totalDays) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.year = year;
        this.totalDays = totalDays;
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

    public Integer getTotalDays() {
        return totalDays;
    }
}
