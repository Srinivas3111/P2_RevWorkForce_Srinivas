package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.DepartmentLeaveReportSummary;
import com.rev.app.dto.EmployeeLeaveBalanceSummary;
import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.dto.EmployeeLeaveQuotaDTO;
import com.rev.app.dto.EmployeeLeaveReportSummary;
import com.rev.app.dto.EmployeeQuotaSummary;
import com.rev.app.dto.LeaveTypeDTO;
import com.rev.app.dto.ManagerTeamLeaveBalanceSummary;
import com.rev.app.entity.Employee;
import com.rev.app.entity.EmployeeLeaveHistory;
import com.rev.app.entity.EmployeeLeaveQuota;
import com.rev.app.entity.LeaveType;
import com.rev.app.mapper.EmployeeLeaveHistoryMapper;
import com.rev.app.mapper.EmployeeLeaveQuotaMapper;
import com.rev.app.mapper.LeaveTypeMapper;
import com.rev.app.repository.EmployeeLeaveHistoryRepository;
import com.rev.app.repository.EmployeeLeaveQuotaRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveManagementServiceImpl implements LeaveManagementService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeLeaveQuotaRepository employeeLeaveQuotaRepository;

    @Autowired
    private EmployeeLeaveHistoryRepository employeeLeaveHistoryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeMapper leaveTypeMapper;

    @Autowired
    private EmployeeLeaveQuotaMapper employeeLeaveQuotaMapper;

    @Autowired
    private EmployeeLeaveHistoryMapper employeeLeaveHistoryMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<EmployeeLeaveHistoryDTO> getManagerLeaveRequests() {
        return employeeLeaveHistoryRepository.findAllManagerLeaveRequests()
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveTypeDTO> getAllLeaveTypes() {
        return leaveTypeRepository.findAllByOrderByNameAsc()
                .stream()
                .map(leaveTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveTypeDTO> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(leaveTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeDTO createLeaveType(String name, String description) throws Exception {
        String cleanName = (name == null) ? "" : name.trim();
        if (cleanName.isEmpty()) {
            throw new Exception("Leave type name is required");
        }

        Optional<LeaveType> existing = leaveTypeRepository.findByNameIgnoreCase(cleanName);
        if (existing.isPresent()) {
            throw new Exception("Leave type already exists");
        }

        LeaveType leaveType = new LeaveType();
        leaveType.setName(cleanName);
        leaveType.setDescription(description == null ? null : description.trim());
        leaveType.setActive(true);
        return leaveTypeMapper.toDTO(leaveTypeRepository.save(leaveType));
    }

    @Override
    public LeaveTypeDTO updateLeaveTypeStatus(Long leaveTypeId, boolean active) throws Exception {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new Exception("Leave type not found"));
        leaveType.setActive(active);
        return leaveTypeMapper.toDTO(leaveTypeRepository.save(leaveType));
    }

    @Override
    public EmployeeLeaveQuotaDTO assignOrUpdateQuota(Long employeeId, Long leaveTypeId, Integer year, Integer quotaDays)
            throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee is required");
        }
        if (leaveTypeId == null) {
            throw new Exception("Leave type is required");
        }
        if (quotaDays == null || quotaDays <= 0) {
            throw new Exception("Quota days must be a positive number");
        }
        if (quotaDays > 366) {
            throw new Exception("Quota days cannot exceed 366");
        }

        int selectedYear = (year == null) ? Year.now().getValue() : year;
        if (selectedYear < 2000 || selectedYear > 2100) {
            throw new Exception("Invalid year");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception("Employee not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new Exception("Leave type not found"));

        if (!employee.isActive()) {
            throw new Exception("Cannot assign quota to an inactive employee");
        }
        if (!leaveType.isActive()) {
            throw new Exception("Cannot assign quota to an inactive leave type");
        }

        Optional<EmployeeLeaveQuota> existing = employeeLeaveQuotaRepository
                .findByEmployee_IdAndLeaveType_IdAndYear(employeeId, leaveTypeId, selectedYear);

        EmployeeLeaveQuota quota = existing.orElseGet(EmployeeLeaveQuota::new);
        quota.setEmployee(employee);
        quota.setLeaveType(leaveType);
        quota.setYear(selectedYear);
        quota.setQuotaDays(quotaDays);
        return employeeLeaveQuotaMapper.toDTO(employeeLeaveQuotaRepository.save(quota));
    }

    @Override
    public List<EmployeeLeaveQuotaDTO> getQuotasByYear(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        return employeeLeaveQuotaRepository.findByYear(selectedYear)
                .stream()
                .sorted(Comparator
                        .comparing((EmployeeLeaveQuota q) -> q.getEmployee().getId())
                        .thenComparing(q -> q.getLeaveType().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(employeeLeaveQuotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeQuotaSummary> getEmployeeQuotaSummaryByYear(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<EmployeeLeaveQuota> quotas = employeeLeaveQuotaRepository.findByYear(selectedYear);
        Map<Long, EmployeeQuotaSummary> summaryMap = new LinkedHashMap<>();

        for (EmployeeLeaveQuota quota : quotas) {
            Long employeeId = quota.getEmployee().getId();
            EmployeeQuotaSummary current = summaryMap.get(employeeId);
            if (current == null) {
                summaryMap.put(employeeId, new EmployeeQuotaSummary(
                        employeeId,
                        quota.getEmployee().getName(),
                        selectedYear,
                        quota.getQuotaDays()));
            } else {
                summaryMap.put(employeeId, new EmployeeQuotaSummary(
                        current.getEmployeeId(),
                        current.getEmployeeName(),
                        selectedYear,
                        current.getTotalDays() + quota.getQuotaDays()));
            }
        }

        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<EmployeeLeaveBalanceSummary> getEmployeeLeaveBalanceByYear(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<EmployeeLeaveQuota> quotas = employeeLeaveQuotaRepository.findByYear(selectedYear);
        List<EmployeeLeaveHistory> approvedHistory = employeeLeaveHistoryRepository
                .findByYearAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(selectedYear, "APPROVED");

        Map<String, Integer> usedDaysByEmployeeLeaveType = new HashMap<>();
        for (EmployeeLeaveHistory history : approvedHistory) {
            String key = buildEmployeeLeaveTypeKey(history.getEmployee().getId(), history.getLeaveType().getId());
            usedDaysByEmployeeLeaveType.merge(key, history.getLeaveDays(), Integer::sum);
        }

        Map<String, EmployeeLeaveBalanceSummary> balances = new LinkedHashMap<>();

        for (EmployeeLeaveQuota quota : quotas) {
            Long employeeId = quota.getEmployee().getId();
            Long leaveTypeId = quota.getLeaveType().getId();
            String key = buildEmployeeLeaveTypeKey(employeeId, leaveTypeId);

            Integer quotaDays = quota.getQuotaDays();
            Integer usedDays = usedDaysByEmployeeLeaveType.getOrDefault(key, 0);
            Integer balanceDays = quotaDays - usedDays;

            balances.put(key, new EmployeeLeaveBalanceSummary(
                    employeeId,
                    quota.getEmployee().getName(),
                    quota.getLeaveType().getName(),
                    selectedYear,
                    quotaDays,
                    usedDays,
                    balanceDays));
        }

        for (EmployeeLeaveHistory history : approvedHistory) {
            Long employeeId = history.getEmployee().getId();
            Long leaveTypeId = history.getLeaveType().getId();
            String key = buildEmployeeLeaveTypeKey(employeeId, leaveTypeId);

            if (balances.containsKey(key)) {
                continue;
            }

            Integer usedDays = usedDaysByEmployeeLeaveType.getOrDefault(key, 0);
            balances.put(key, new EmployeeLeaveBalanceSummary(
                    employeeId,
                    history.getEmployee().getName(),
                    history.getLeaveType().getName(),
                    selectedYear,
                    0,
                    usedDays,
                    -usedDays));
        }

        return balances.values()
                .stream()
                .sorted(Comparator
                        .comparing(EmployeeLeaveBalanceSummary::getEmployeeId)
                        .thenComparing(EmployeeLeaveBalanceSummary::getLeaveTypeName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public List<EmployeeLeaveBalanceSummary> getEmployeeLeaveBalanceByYear(Long employeeId, Integer year) {
        if (employeeId == null) {
            return List.of();
        }

        return getEmployeeLeaveBalanceByYear(year)
                .stream()
                .filter(r -> employeeId.equals(r.getEmployeeId()))
                .sorted(Comparator.comparing(EmployeeLeaveBalanceSummary::getLeaveTypeName,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public List<EmployeeLeaveHistoryDTO> getLeaveHistoryByYear(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        return employeeLeaveHistoryRepository.findByYearOrderByAppliedOnDescStartDateDesc(selectedYear)
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeLeaveHistoryDTO> getEmployeeLeaveApplications(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return employeeLeaveHistoryRepository.findByEmployee_IdOrderByAppliedOnDescStartDateDesc(employeeId)
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeLeaveHistoryDTO cancelEmployeeLeaveRequest(Long employeeId, Long leaveHistoryId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee identity is required.");
        }
        if (leaveHistoryId == null) {
            throw new Exception("Leave request ID is required.");
        }

        EmployeeLeaveHistory leaveHistory = employeeLeaveHistoryRepository
                .findByIdAndEmployee_Id(leaveHistoryId, employeeId)
                .orElseThrow(() -> new Exception("Leave request not found for this employee."));

        String status = leaveHistory.getStatus() == null ? "" : leaveHistory.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(status)) {
            throw new Exception("Only pending leave requests can be cancelled.");
        }

        LocalDate startDate = leaveHistory.getStartDate();
        if (startDate == null || !startDate.isAfter(LocalDate.now())) {
            throw new Exception("Leave cannot be cancelled because it has already started.");
        }

        leaveHistory.setStatus("CANCELLED");
        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveHistory);

        Employee employee = saved.getEmployee();
        String employeeRole = employee.getRole() == null ? "" : employee.getRole().trim().toUpperCase();
        if ("EMPLOYEE".equals(employeeRole) && employee.getManager() != null) {
            String employeeFullName = employee.getName().trim();
            notificationService.createNotification(
                    employee.getManager(),
                    "Team Leave Cancelled",
                    "Action: Leave Cancelled | Employee: " + employeeFullName + " | Leave: "
                            + saved.getLeaveType().getName() + " (" + saved.getStartDate()
                            + (saved.getStartDate().equals(saved.getEndDate()) ? "" : " to " + saved.getEndDate())
                            + ")");
        }

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public List<EmployeeLeaveHistoryDTO> getTeamLeaveApplications(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return employeeLeaveHistoryRepository.findByEmployee_Manager_IdOrderByAppliedOnDescStartDateDesc(managerId)
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeLeaveHistoryDTO> getPendingTeamLeaveApplications(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return employeeLeaveHistoryRepository
                .findByEmployee_Manager_IdAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(managerId, "PENDING")
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeLeaveHistoryDTO> getApprovedTeamLeaveApplications(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return employeeLeaveHistoryRepository
                .findByEmployee_Manager_IdAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(managerId, "APPROVED")
                .stream()
                .map(employeeLeaveHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeLeaveHistoryDTO submitLeaveRequest(Long employeeId,
            Long leaveTypeId,
            LocalDate startDate,
            LocalDate endDate,
            String reason) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee is required.");
        }
        if (leaveTypeId == null) {
            throw new Exception("Leave type is required.");
        }
        if (startDate == null || endDate == null) {
            throw new Exception("Start date and end date are required.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new Exception("Start date cannot be a past date.");
        }
        if (endDate.isBefore(startDate)) {
            throw new Exception("End date cannot be before start date.");
        }
        if (startDate.getYear() != endDate.getYear()) {
            throw new Exception("Leave request should be within the same year.");
        }

        String cleanReason = (reason == null) ? "" : reason.trim();
        if (cleanReason.isEmpty()) {
            throw new Exception("Reason is required.");
        }
        if (cleanReason.length() < 5) {
            throw new Exception("Reason must be at least 5 characters.");
        }
        if (cleanReason.length() > 250) {
            cleanReason = cleanReason.substring(0, 250);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception("Employee not found."));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new Exception("Leave type not found."));

        if (!employee.isActive()) {
            throw new Exception("Inactive employees cannot submit leave requests.");
        }
        if (!leaveType.isActive()) {
            throw new Exception("Selected leave type is inactive.");
        }

        List<EmployeeLeaveHistory> overlappingRequests = employeeLeaveHistoryRepository
                .findOverlappingActiveLeaveRequests(employeeId, startDate, endDate);
        if (!overlappingRequests.isEmpty()) {
            throw new Exception("Leave request overlaps with an existing pending or approved leave.");
        }

        int leaveDays = (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
        int availableLeaveBalance = getAvailableLeaveBalance(employeeId, leaveTypeId, startDate.getYear());
        if (leaveDays > availableLeaveBalance) {
            throw new Exception("Insufficient leave balance");
        }

        EmployeeLeaveHistory leaveRequest = new EmployeeLeaveHistory();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setYear(startDate.getYear());
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setLeaveDays(leaveDays);
        leaveRequest.setStatus("PENDING");
        leaveRequest.setReason(cleanReason);
        leaveRequest.setAppliedOn(LocalDate.now());
        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveRequest);

        String employeeFullName = employee.getName().trim();
        String leaveTypeName = leaveType.getName();
        String dateRange = startDate + (startDate.equals(endDate) ? "" : " to " + endDate);

        // If submitter is a MANAGER → notify Admin; otherwise notify their assigned
        // Manager
        String role = employee.getRole() == null ? "" : employee.getRole().toUpperCase().trim();
        if ("MANAGER".equals(role)) {
            String notifTitle = "Manager Leave Request Submitted";
            String notifMessage = "Action: Leave Submitted | Employee: " + employeeFullName
                    + " | Leave: " + leaveTypeName + " (" + dateRange + ")"
                    + " | Reason: " + cleanReason;
            notificationService.createNotificationForRole("ADMIN", notifTitle, notifMessage);
        } else if (employee.getManager() != null) {
            String notifTitle = "New Leave Request from " + employeeFullName;
            String notifMessage = "Action: Leave Submitted | Employee: " + employeeFullName
                    + " | Leave: " + leaveTypeName + " (" + dateRange + ")"
                    + " | Reason: " + cleanReason;
            notificationService.createNotification(employee.getManager(), notifTitle, notifMessage);
        }

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public EmployeeLeaveHistoryDTO adminApproveLeaveRequest(Long leaveHistoryId, String adminComment)
            throws Exception {
        if (leaveHistoryId == null) {
            throw new Exception("Leave request ID is required");
        }
        EmployeeLeaveHistory leaveHistory = employeeLeaveHistoryRepository.findById(leaveHistoryId)
                .orElseThrow(() -> new Exception("Leave request not found."));
        String currentStatus = leaveHistory.getStatus() == null ? "" : leaveHistory.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus)) {
            throw new Exception("Only pending leave requests can be approved.");
        }
        leaveHistory.setStatus("APPROVED");
        String cleanComment = (adminComment == null) ? "" : adminComment.trim();
        leaveHistory.setManagerComment(cleanComment.isEmpty() ? "Approved by Admin" : cleanComment);
        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveHistory);

        // Notify employee
        notificationService.createNotification(
                saved.getEmployee(),
                "Leave Request Approved",
                "Action: Leave Approved | Employee: " + saved.getEmployee().getName() + " | Leave: "
                        + saved.getLeaveType().getName() + " (" + saved.getStartDate() + ") | Approved By: Admin");

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public EmployeeLeaveHistoryDTO adminRejectLeaveRequest(Long leaveHistoryId, String adminComment)
            throws Exception {
        if (leaveHistoryId == null) {
            throw new Exception("Leave request ID is required");
        }
        EmployeeLeaveHistory leaveHistory = employeeLeaveHistoryRepository.findById(leaveHistoryId)
                .orElseThrow(() -> new Exception("Leave request not found."));
        String currentStatus = leaveHistory.getStatus() == null ? "" : leaveHistory.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus)) {
            throw new Exception("Only pending leave requests can be rejected.");
        }
        leaveHistory.setStatus("REJECTED");
        String cleanComment = (adminComment == null) ? "" : adminComment.trim();
        leaveHistory.setManagerComment(cleanComment.isEmpty() ? "Rejected by Admin" : cleanComment);
        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveHistory);

        // Notify employee
        notificationService.createNotification(
                saved.getEmployee(),
                "Leave Request Rejected",
                "Action: Leave Rejected | Employee: " + saved.getEmployee().getName() + " | Leave: "
                        + saved.getLeaveType().getName() + " (" + saved.getStartDate() + ") | Rejected By: Admin"
                        + " | Reason: " + saved.getManagerComment());

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public EmployeeLeaveHistoryDTO approveTeamLeaveRequest(Long managerId, Long leaveHistoryId, String managerComment)
            throws Exception {
        if (managerId == null) {
            throw new Exception("Manager identity is required");
        }
        if (leaveHistoryId == null) {
            throw new Exception("Leave request ID is required");
        }

        EmployeeLeaveHistory leaveHistory = employeeLeaveHistoryRepository
                .findByIdAndEmployee_Manager_Id(leaveHistoryId, managerId)
                .orElseThrow(() -> new Exception("Leave request not found for your team."));

        String currentStatus = leaveHistory.getStatus() == null ? "" : leaveHistory.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus)) {
            throw new Exception("Only pending leave requests can be approved.");
        }

        leaveHistory.setStatus("APPROVED");

        String cleanComment = (managerComment == null) ? "" : managerComment.trim();
        if (cleanComment.isEmpty()) {
            leaveHistory.setManagerComment(null);
        } else if (cleanComment.length() > 500) {
            leaveHistory.setManagerComment(cleanComment.substring(0, 500));
        } else {
            leaveHistory.setManagerComment(cleanComment);
        }

        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveHistory);

        // Notify employee
        notificationService.createNotification(
                saved.getEmployee(),
                "Leave Request Approved",
                "Action: Leave Approved | Employee: " + saved.getEmployee().getName() + " | Leave: "
                        + saved.getLeaveType().getName() + " (" + saved.getStartDate() + ") | Approved By: Manager");

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public EmployeeLeaveHistoryDTO rejectTeamLeaveRequest(Long managerId, Long leaveHistoryId, String managerComment)
            throws Exception {
        if (managerId == null) {
            throw new Exception("Manager identity is required");
        }
        if (leaveHistoryId == null) {
            throw new Exception("Leave request ID is required");
        }

        EmployeeLeaveHistory leaveHistory = employeeLeaveHistoryRepository
                .findByIdAndEmployee_Manager_Id(leaveHistoryId, managerId)
                .orElseThrow(() -> new Exception("Leave request not found for your team."));

        String currentStatus = leaveHistory.getStatus() == null ? "" : leaveHistory.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus)) {
            throw new Exception("Only pending leave requests can be rejected.");
        }

        String cleanComment = (managerComment == null) ? "" : managerComment.trim();
        if (cleanComment.isEmpty()) {
            throw new Exception("Comment is mandatory when rejecting a leave request.");
        }
        if (cleanComment.length() > 500) {
            cleanComment = cleanComment.substring(0, 500);
        }

        leaveHistory.setStatus("REJECTED");
        leaveHistory.setManagerComment(cleanComment);
        EmployeeLeaveHistory saved = employeeLeaveHistoryRepository.save(leaveHistory);

        // Notify employee
        notificationService.createNotification(
                saved.getEmployee(),
                "Leave Request Rejected",
                "Action: Leave Rejected | Employee: " + saved.getEmployee().getName() + " | Leave: "
                        + saved.getLeaveType().getName() + " (" + saved.getStartDate() + ") | Rejected By: Manager"
                        + " | Reason: " + cleanComment);

        return employeeLeaveHistoryMapper.toDTO(saved);
    }

    @Override
    public List<ManagerTeamLeaveBalanceSummary> getManagerTeamLeaveBalance(Long managerId, Integer year) {
        if (managerId == null) {
            return List.of();
        }

        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<EmployeeLeaveBalanceSummary> allBalances = getEmployeeLeaveBalanceByYear(selectedYear);
        List<Employee> teamMembers = employeeRepository.findByManager_IdAndActiveTrueOrderByIdAsc(managerId);
        Set<Long> teamMemberIds = new HashSet<>();
        for (Employee member : teamMembers) {
            teamMemberIds.add(member.getId());
        }

        Map<Long, MutableTeamLeaveBalance> grouped = new LinkedHashMap<>();
        for (EmployeeLeaveBalanceSummary balance : allBalances) {
            if (!teamMemberIds.contains(balance.getEmployeeId())) {
                continue;
            }
            MutableTeamLeaveBalance current = grouped.computeIfAbsent(
                    balance.getEmployeeId(),
                    id -> new MutableTeamLeaveBalance(id, balance.getEmployeeName(), selectedYear));
            current.totalAllowedLeaves += safeInt(balance.getQuotaDays());
            current.usedLeaves += safeInt(balance.getUsedDays());
            current.remainingLeaves += safeInt(balance.getBalanceDays());
            current.details.add(balance);
        }

        for (Employee member : teamMembers) {
            grouped.computeIfAbsent(
                    member.getId(),
                    id -> new MutableTeamLeaveBalance(
                            member.getId(),
                            member.getName(),
                            selectedYear));
        }

        return grouped.values().stream()
                .sorted(Comparator.comparing(r -> r.employeeId))
                .map(r -> {
                    ManagerTeamLeaveBalanceSummary summary = new ManagerTeamLeaveBalanceSummary(
                            r.employeeId,
                            r.employeeName,
                            r.year,
                            r.totalAllowedLeaves,
                            r.usedLeaves,
                            r.remainingLeaves);
                    summary.setDetails(r.details);
                    return summary;
                })
                .toList();
    }

    @Override
    public List<EmployeeLeaveReportSummary> getEmployeeWiseLeaveReport(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<Employee> employees = employeeRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Employee::getId))
                .toList();
        List<EmployeeLeaveQuota> quotas = employeeLeaveQuotaRepository.findByYear(selectedYear);
        List<EmployeeLeaveHistory> history = employeeLeaveHistoryRepository
                .findByYearOrderByAppliedOnDescStartDateDesc(selectedYear);

        Map<Long, MutableEmployeeReport> employeeReportMap = new LinkedHashMap<>();

        for (Employee employee : employees) {
            employeeReportMap.put(employee.getId(), new MutableEmployeeReport(
                    employee.getId(),
                    employee.getName(),
                    normalizeDepartment(employee.getDepartment())));
        }

        for (EmployeeLeaveQuota quota : quotas) {
            MutableEmployeeReport report = employeeReportMap.computeIfAbsent(
                    quota.getEmployee().getId(),
                    id -> new MutableEmployeeReport(
                            quota.getEmployee().getId(),
                            quota.getEmployee().getName(),
                            normalizeDepartment(quota.getEmployee().getDepartment())));
            report.quotaDays += quota.getQuotaDays();
        }

        for (EmployeeLeaveHistory h : history) {
            MutableEmployeeReport report = employeeReportMap.computeIfAbsent(
                    h.getEmployee().getId(),
                    id -> new MutableEmployeeReport(
                            h.getEmployee().getId(),
                            h.getEmployee().getName(),
                            normalizeDepartment(h.getEmployee().getDepartment())));

            report.totalRequests += 1;
            String status = (h.getStatus() == null) ? "" : h.getStatus().trim().toUpperCase();
            if ("APPROVED".equals(status)) {
                report.approvedRequests += 1;
                report.usedDays += h.getLeaveDays();
            } else if ("REJECTED".equals(status)) {
                report.rejectedRequests += 1;
            } else {
                report.pendingRequests += 1;
            }
        }

        return employeeReportMap.values()
                .stream()
                .sorted(Comparator.comparing(r -> r.employeeName, String.CASE_INSENSITIVE_ORDER))
                .map(r -> new EmployeeLeaveReportSummary(
                        r.employeeId,
                        r.employeeName,
                        r.departmentName,
                        r.quotaDays,
                        r.usedDays,
                        r.quotaDays - r.usedDays,
                        r.totalRequests,
                        r.approvedRequests,
                        r.pendingRequests,
                        r.rejectedRequests))
                .toList();
    }

    @Override
    public List<DepartmentLeaveReportSummary> getDepartmentWiseLeaveReport(Integer year) {
        List<EmployeeLeaveReportSummary> employeeReports = getEmployeeWiseLeaveReport(year);
        Map<String, MutableDepartmentReport> departmentMap = new LinkedHashMap<>();

        for (EmployeeLeaveReportSummary employeeReport : employeeReports) {
            MutableDepartmentReport departmentReport = departmentMap.computeIfAbsent(
                    employeeReport.getDepartmentName(),
                    dept -> new MutableDepartmentReport(dept));

            departmentReport.employeeCount += 1;
            departmentReport.totalQuotaDays += employeeReport.getQuotaDays();
            departmentReport.totalUsedDays += employeeReport.getUsedDays();
            departmentReport.totalBalanceDays += employeeReport.getBalanceDays();
            departmentReport.totalRequests += employeeReport.getTotalRequests();
            departmentReport.approvedRequests += employeeReport.getApprovedRequests();
            departmentReport.pendingRequests += employeeReport.getPendingRequests();
            departmentReport.rejectedRequests += employeeReport.getRejectedRequests();
        }

        return departmentMap.values()
                .stream()
                .sorted(Comparator.comparing(r -> r.departmentName, String.CASE_INSENSITIVE_ORDER))
                .map(r -> new DepartmentLeaveReportSummary(
                        r.departmentName,
                        r.employeeCount,
                        r.totalQuotaDays,
                        r.totalUsedDays,
                        r.totalBalanceDays,
                        r.totalRequests,
                        r.approvedRequests,
                        r.pendingRequests,
                        r.rejectedRequests))
                .toList();
    }

    private String buildEmployeeLeaveTypeKey(Long employeeId, Long leaveTypeId) {
        return employeeId + "-" + leaveTypeId;
    }

    private int getAvailableLeaveBalance(Long employeeId, Long leaveTypeId, Integer year) {
        int quotaDays = employeeLeaveQuotaRepository
                .findByEmployee_IdAndLeaveType_IdAndYear(employeeId, leaveTypeId, year)
                .map(q -> q.getQuotaDays() == null ? 0 : q.getQuotaDays())
                .orElse(0);

        int usedDays = Optional.ofNullable(
                employeeLeaveHistoryRepository.sumApprovedLeaveDaysByEmployeeAndLeaveTypeAndYear(
                        employeeId,
                        leaveTypeId,
                        year))
                .orElse(0);

        return Math.max(0, quotaDays - usedDays);
    }

    private String normalizeDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return "Unassigned";
        }
        return department.trim();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static class MutableEmployeeReport {
        private final Long employeeId;
        private final String employeeName;
        private final String departmentName;
        private int quotaDays = 0;
        private int usedDays = 0;
        private int totalRequests = 0;
        private int approvedRequests = 0;
        private int pendingRequests = 0;
        private int rejectedRequests = 0;

        private MutableEmployeeReport(Long employeeId, String employeeName, String departmentName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.departmentName = departmentName;
        }
    }

    private static class MutableDepartmentReport {
        private final String departmentName;
        private int employeeCount = 0;
        private int totalQuotaDays = 0;
        private int totalUsedDays = 0;
        private int totalBalanceDays = 0;
        private int totalRequests = 0;
        private int approvedRequests = 0;
        private int pendingRequests = 0;
        private int rejectedRequests = 0;

        private MutableDepartmentReport(String departmentName) {
            this.departmentName = departmentName;
        }
    }

    private static class MutableTeamLeaveBalance {
        private final Long employeeId;
        private final String employeeName;
        private final Integer year;
        private int totalAllowedLeaves = 0;
        private int usedLeaves = 0;
        private int remainingLeaves = 0;
        private List<EmployeeLeaveBalanceSummary> details = new ArrayList<>();

        private MutableTeamLeaveBalance(Long employeeId, String employeeName, Integer year) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.year = year;
        }
    }
}
