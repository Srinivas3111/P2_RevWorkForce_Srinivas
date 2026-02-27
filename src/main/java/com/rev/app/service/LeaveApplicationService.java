package com.rev.app.service;

import com.rev.app.entity.*;
import com.rev.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private NotificationService notificationService;

    // ========== APPLY FOR LEAVE ==========

    public LeaveApplication applyLeave(Integer empId, Integer leaveTypeId,
            LocalDate startDate, LocalDate endDate, String reason) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + leaveTypeId));

        // Check leave balance
        Optional<LeaveBalance> balance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType);
        long requestedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (balance.isPresent() && balance.get().getBalanceDays() < requestedDays) {
            throw new RuntimeException("Insufficient leave balance. Available: " +
                    balance.get().getBalanceDays() + ", Requested: " + requestedDays);
        }

        LeaveApplication application = new LeaveApplication(employee, leaveType, startDate, endDate, reason);
        LeaveApplication saved = leaveApplicationRepository.save(application);

        // Notify manager
        if (employee.getManager() != null && employee.getManager().getUser() != null) {
            notificationService.createNotification(
                    employee.getManager().getUser().getUserId(),
                    employee.getFirstName() + " " + employee.getLastName() + " has applied for " +
                            leaveType.getLeaveName() + " from " + startDate + " to " + endDate,
                    "LEAVE_REQUEST");
        }

        return saved;
    }

    // ========== VIEW LEAVES ==========

    public List<LeaveApplication> getLeavesByEmployee(Integer empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
        return leaveApplicationRepository.findByEmployeeOrderByAppliedOnDesc(employee);
    }

    public List<LeaveApplication> getLeavesByStatus(String status) {
        return leaveApplicationRepository.findByStatus(status);
    }

    public List<LeaveApplication> getPendingLeavesForManager(Integer managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
        List<Employee> reportees = employeeRepository.findByManager(manager);
        return leaveApplicationRepository.findByEmployeeInAndStatus(reportees, "PENDING");
    }

    public List<LeaveApplication> getAllLeavesForManager(Integer managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
        List<Employee> reportees = employeeRepository.findByManager(manager);
        return leaveApplicationRepository.findByEmployeeIn(reportees);
    }

    // ========== APPROVE / REJECT ==========

    public LeaveApplication approveLeave(Integer leaveId, Integer approvedByEmpId, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with ID: " + leaveId));
        Employee approvedBy = employeeRepository.findById(approvedByEmpId)
                .orElseThrow(() -> new RuntimeException("Approver not found with ID: " + approvedByEmpId));

        application.setStatus("APPROVED");
        application.setApprovedBy(approvedBy);
        application.setManagerComment(comment);

        // Deduct leave balance
        Optional<LeaveBalance> balance = leaveBalanceRepository.findByEmployeeAndLeaveType(
                application.getEmployee(), application.getLeaveType());
        if (balance.isPresent()) {
            long days = ChronoUnit.DAYS.between(application.getStartDate(), application.getEndDate()) + 1;
            balance.get().setBalanceDays(balance.get().getBalanceDays() - (int) days);
            leaveBalanceRepository.save(balance.get());
        }

        // Notify employee
        notificationService.createNotification(
                application.getEmployee().getUser().getUserId(),
                "Your leave request from " + application.getStartDate() + " to " + application.getEndDate()
                        + " has been APPROVED.",
                "LEAVE_APPROVED");

        return leaveApplicationRepository.save(application);
    }

    public LeaveApplication rejectLeave(Integer leaveId, Integer rejectedByEmpId, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with ID: " + leaveId));
        Employee rejectedBy = employeeRepository.findById(rejectedByEmpId)
                .orElseThrow(() -> new RuntimeException("Rejector not found with ID: " + rejectedByEmpId));

        application.setStatus("REJECTED");
        application.setApprovedBy(rejectedBy);
        application.setManagerComment(comment);

        // Notify employee
        notificationService.createNotification(
                application.getEmployee().getUser().getUserId(),
                "Your leave request from " + application.getStartDate() + " to " + application.getEndDate() +
                        " has been REJECTED. Reason: " + comment,
                "LEAVE_REJECTED");

        return leaveApplicationRepository.save(application);
    }

    // ========== CANCEL ==========

    public LeaveApplication cancelLeave(Integer leaveId) {
        LeaveApplication application = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with ID: " + leaveId));

        if ("APPROVED".equals(application.getStatus())) {
            // Restore leave balance
            Optional<LeaveBalance> balance = leaveBalanceRepository.findByEmployeeAndLeaveType(
                    application.getEmployee(), application.getLeaveType());
            if (balance.isPresent()) {
                long days = ChronoUnit.DAYS.between(application.getStartDate(), application.getEndDate()) + 1;
                balance.get().setBalanceDays(balance.get().getBalanceDays() + (int) days);
                leaveBalanceRepository.save(balance.get());
            }
        }

        application.setStatus("CANCELLED");
        return leaveApplicationRepository.save(application);
    }

    public Optional<LeaveApplication> getLeaveById(Integer leaveId) {
        return leaveApplicationRepository.findById(leaveId);
    }
}
