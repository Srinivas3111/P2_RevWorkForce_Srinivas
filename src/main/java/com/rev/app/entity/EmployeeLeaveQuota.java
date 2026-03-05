package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_leave_quotas", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "employee_id", "leave_type_id", "quota_year" })
})
public class EmployeeLeaveQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_leave_quota_seq")
    @SequenceGenerator(name = "emp_leave_quota_seq", sequenceName = "emp_leave_quota_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "quota_year", nullable = false)
    private Integer year;

    @Column(name = "quota_days", nullable = false)
    private Integer quotaDays;

    public EmployeeLeaveQuota() {
    }

    public EmployeeLeaveQuota(Employee employee, LeaveType leaveType, Integer year, Integer quotaDays) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.year = year;
        this.quotaDays = quotaDays;
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

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getQuotaDays() {
        return quotaDays;
    }

    public void setQuotaDays(Integer quotaDays) {
        this.quotaDays = quotaDays;
    }
}
