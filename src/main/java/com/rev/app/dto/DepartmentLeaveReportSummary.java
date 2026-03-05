package com.rev.app.dto;

public class DepartmentLeaveReportSummary {
    private String departmentName;
    private Integer employeeCount;
    private Integer totalQuotaDays;
    private Integer totalUsedDays;
    private Integer totalBalanceDays;
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer pendingRequests;
    private Integer rejectedRequests;

    public DepartmentLeaveReportSummary(String departmentName,
                                        Integer employeeCount,
                                        Integer totalQuotaDays,
                                        Integer totalUsedDays,
                                        Integer totalBalanceDays,
                                        Integer totalRequests,
                                        Integer approvedRequests,
                                        Integer pendingRequests,
                                        Integer rejectedRequests) {
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.totalQuotaDays = totalQuotaDays;
        this.totalUsedDays = totalUsedDays;
        this.totalBalanceDays = totalBalanceDays;
        this.totalRequests = totalRequests;
        this.approvedRequests = approvedRequests;
        this.pendingRequests = pendingRequests;
        this.rejectedRequests = rejectedRequests;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public Integer getTotalQuotaDays() {
        return totalQuotaDays;
    }

    public Integer getTotalUsedDays() {
        return totalUsedDays;
    }

    public Integer getTotalBalanceDays() {
        return totalBalanceDays;
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
