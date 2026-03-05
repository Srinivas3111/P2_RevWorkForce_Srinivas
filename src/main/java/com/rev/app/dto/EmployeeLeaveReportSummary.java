package com.rev.app.dto;

public class EmployeeLeaveReportSummary {
    private Long employeeId;
    private String employeeName;
    private String departmentName;
    private Integer quotaDays;
    private Integer usedDays;
    private Integer balanceDays;
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer pendingRequests;
    private Integer rejectedRequests;

    public EmployeeLeaveReportSummary(Long employeeId,
                                      String employeeName,
                                      String departmentName,
                                      Integer quotaDays,
                                      Integer usedDays,
                                      Integer balanceDays,
                                      Integer totalRequests,
                                      Integer approvedRequests,
                                      Integer pendingRequests,
                                      Integer rejectedRequests) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.quotaDays = quotaDays;
        this.usedDays = usedDays;
        this.balanceDays = balanceDays;
        this.totalRequests = totalRequests;
        this.approvedRequests = approvedRequests;
        this.pendingRequests = pendingRequests;
        this.rejectedRequests = rejectedRequests;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
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

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public Integer getApprovedRequests() {
        return approvedRequests;
    }

    public Integer getPendingRequests() {
        return pendingRequests;
    }

    public Integer getRejectedRequests() {
        return rejectedRequests;
    }
}
