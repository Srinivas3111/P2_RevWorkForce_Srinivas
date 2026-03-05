package com.rev.app.dto;

public class EmployeeLeaveBalanceSummary {
    private Long employeeId;
    private String employeeName;
    private String leaveTypeName;
    private Integer year;
    private Integer quotaDays;
    private Integer usedDays;
    private Integer balanceDays;

    public EmployeeLeaveBalanceSummary(Long employeeId,
                                       String employeeName,
                                       String leaveTypeName,
                                       Integer year,
                                       Integer quotaDays,
                                       Integer usedDays,
                                       Integer balanceDays) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.leaveTypeName = leaveTypeName;
        this.year = year;
        this.quotaDays = quotaDays;
        this.usedDays = usedDays;
        this.balanceDays = balanceDays;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getQuotaDays() {
        return quotaDays;
    }

    public Integer getUsedDays() {
        return usedDays;
    }

    public Integer getBalanceDays() {
        return balanceDays;
    }
}
