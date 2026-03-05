package com.rev.app.service;

import com.rev.app.dto.DepartmentLeaveReportSummary;
import com.rev.app.dto.EmployeeLeaveBalanceSummary;
import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.dto.EmployeeLeaveQuotaDTO;
import com.rev.app.dto.EmployeeLeaveReportSummary;
import com.rev.app.dto.EmployeeQuotaSummary;
import com.rev.app.dto.LeaveTypeDTO;
import com.rev.app.dto.ManagerTeamLeaveBalanceSummary;

import java.time.LocalDate;
import java.util.List;

public interface LeaveManagementService {
    List<EmployeeLeaveHistoryDTO> getManagerLeaveRequests();

    List<LeaveTypeDTO> getAllLeaveTypes();

    List<LeaveTypeDTO> getActiveLeaveTypes();

    LeaveTypeDTO createLeaveType(String name, String description) throws Exception;

    LeaveTypeDTO updateLeaveTypeStatus(Long leaveTypeId, boolean active) throws Exception;

    EmployeeLeaveQuotaDTO assignOrUpdateQuota(Long employeeId, Long leaveTypeId, Integer year, Integer quotaDays) throws Exception;

    List<EmployeeLeaveQuotaDTO> getQuotasByYear(Integer year);

    List<EmployeeQuotaSummary> getEmployeeQuotaSummaryByYear(Integer year);

    List<EmployeeLeaveBalanceSummary> getEmployeeLeaveBalanceByYear(Integer year);

    List<EmployeeLeaveBalanceSummary> getEmployeeLeaveBalanceByYear(Long employeeId, Integer year);

    List<EmployeeLeaveHistoryDTO> getLeaveHistoryByYear(Integer year);

    List<EmployeeLeaveHistoryDTO> getEmployeeLeaveApplications(Long employeeId);

    EmployeeLeaveHistoryDTO cancelEmployeeLeaveRequest(Long employeeId, Long leaveHistoryId) throws Exception;

    List<EmployeeLeaveHistoryDTO> getTeamLeaveApplications(Long managerId);

    List<EmployeeLeaveHistoryDTO> getPendingTeamLeaveApplications(Long managerId);

    List<EmployeeLeaveHistoryDTO> getApprovedTeamLeaveApplications(Long managerId);

    EmployeeLeaveHistoryDTO submitLeaveRequest(Long employeeId,
                                               Long leaveTypeId,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               String reason) throws Exception;

    EmployeeLeaveHistoryDTO adminApproveLeaveRequest(Long leaveHistoryId, String adminComment) throws Exception;

    EmployeeLeaveHistoryDTO adminRejectLeaveRequest(Long leaveHistoryId, String adminComment) throws Exception;

    EmployeeLeaveHistoryDTO approveTeamLeaveRequest(Long managerId, Long leaveHistoryId, String managerComment) throws Exception;

    EmployeeLeaveHistoryDTO rejectTeamLeaveRequest(Long managerId, Long leaveHistoryId, String managerComment) throws Exception;

    List<ManagerTeamLeaveBalanceSummary> getManagerTeamLeaveBalance(Long managerId, Integer year);

    List<EmployeeLeaveReportSummary> getEmployeeWiseLeaveReport(Integer year);

    List<DepartmentLeaveReportSummary> getDepartmentWiseLeaveReport(Integer year);
}
