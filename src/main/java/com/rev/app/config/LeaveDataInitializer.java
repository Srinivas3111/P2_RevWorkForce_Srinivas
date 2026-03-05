package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.entity.EmployeeLeaveHistory;
import com.rev.app.entity.EmployeeLeaveQuota;
import com.rev.app.entity.LeaveType;
import com.rev.app.repository.EmployeeLeaveHistoryRepository;
import com.rev.app.repository.EmployeeLeaveQuotaRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Component
@Order(2)
public class LeaveDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveDataInitializer.class);

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeLeaveQuotaRepository employeeLeaveQuotaRepository;

    @Autowired
    private EmployeeLeaveHistoryRepository employeeLeaveHistoryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        try {
            LeaveType casualLeave = ensureLeaveType("Casual Leave", "Leave for personal planned needs");
            LeaveType sickLeave = ensureLeaveType("Sick Leave", "Leave for illness or medical reasons");
            LeaveType paidLeave = ensureLeaveType("Paid Leave", "General paid time off");

            int currentYear = Year.now().getValue();
            Optional<Employee> sampleEmployee = employeeRepository.findById(4L);
            if (sampleEmployee.isPresent()) {
                ensureQuota(sampleEmployee.get(), casualLeave, currentYear, 12);
                ensureQuota(sampleEmployee.get(), sickLeave, currentYear, 8);
                ensureQuota(sampleEmployee.get(), paidLeave, currentYear, 15);

                ensureHistory(sampleEmployee.get(), casualLeave, currentYear,
                        LocalDate.of(currentYear, 1, 10), LocalDate.of(currentYear, 1, 12),
                        3, "APPROVED", "Family event", LocalDate.of(currentYear, 1, 5));
                ensureHistory(sampleEmployee.get(), sickLeave, currentYear,
                        LocalDate.of(currentYear, 2, 14), LocalDate.of(currentYear, 2, 15),
                        2, "APPROVED", "Fever and recovery", LocalDate.of(currentYear, 2, 13));
                ensureHistory(sampleEmployee.get(), paidLeave, currentYear,
                        LocalDate.of(currentYear, 3, 22), LocalDate.of(currentYear, 3, 24),
                        3, "PENDING", "Personal travel plan", LocalDate.of(currentYear, 3, 10));
            }
        } catch (Exception e) {
            LOGGER.error("LEAVE SEED ERROR: {}", e.getMessage(), e);
        }
    }

    private LeaveType ensureLeaveType(String name, String description) {
        return leaveTypeRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            LeaveType leaveType = new LeaveType();
            leaveType.setName(name);
            leaveType.setDescription(description);
            leaveType.setActive(true);
            return leaveTypeRepository.save(leaveType);
        });
    }

    private void ensureQuota(Employee employee, LeaveType leaveType, int year, int quotaDays) {
        Optional<EmployeeLeaveQuota> existing = employeeLeaveQuotaRepository
                .findByEmployee_IdAndLeaveType_IdAndYear(employee.getId(), leaveType.getId(), year);
        if (existing.isEmpty()) {
            EmployeeLeaveQuota quota = new EmployeeLeaveQuota();
            quota.setEmployee(employee);
            quota.setLeaveType(leaveType);
            quota.setYear(year);
            quota.setQuotaDays(quotaDays);
            employeeLeaveQuotaRepository.save(quota);
        }
    }

    private void ensureHistory(Employee employee,
                               LeaveType leaveType,
                               int year,
                               LocalDate startDate,
                               LocalDate endDate,
                               int leaveDays,
                               String status,
                               String reason,
                               LocalDate appliedOn) {
        Optional<EmployeeLeaveHistory> existing = employeeLeaveHistoryRepository
                .findByEmployee_IdAndLeaveType_IdAndStartDateAndEndDate(
                        employee.getId(),
                        leaveType.getId(),
                        startDate,
                        endDate);

        if (existing.isEmpty()) {
            EmployeeLeaveHistory history = new EmployeeLeaveHistory();
            history.setEmployee(employee);
            history.setLeaveType(leaveType);
            history.setYear(year);
            history.setStartDate(startDate);
            history.setEndDate(endDate);
            history.setLeaveDays(leaveDays);
            history.setStatus(status);
            history.setReason(reason);
            history.setAppliedOn(appliedOn);
            employeeLeaveHistoryRepository.save(history);
        }
    }
}
