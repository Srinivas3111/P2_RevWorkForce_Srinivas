package com.rev.app.service;

import com.rev.app.service.impl.LeaveManagementServiceImpl;

import com.rev.app.dto.DepartmentLeaveReportSummary;
import com.rev.app.dto.EmployeeLeaveBalanceSummary;
import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.dto.EmployeeLeaveQuotaDTO;
import com.rev.app.dto.EmployeeLeaveReportSummary;
import com.rev.app.entity.Employee;
import com.rev.app.entity.EmployeeLeaveHistory;
import com.rev.app.entity.EmployeeLeaveQuota;
import com.rev.app.entity.LeaveType;
import com.rev.app.mapper.EmployeeLeaveHistoryMapper;
import com.rev.app.mapper.EmployeeLeaveQuotaMapper;
import com.rev.app.repository.EmployeeLeaveHistoryRepository;
import com.rev.app.repository.EmployeeLeaveQuotaRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveManagementServiceTest {

        @Mock
        private LeaveTypeRepository leaveTypeRepository;

        @Mock
        private EmployeeLeaveQuotaRepository employeeLeaveQuotaRepository;

        @Mock
        private EmployeeLeaveHistoryRepository employeeLeaveHistoryRepository;

        @Mock
        private EmployeeRepository employeeRepository;

        @Mock
        private EmployeeLeaveQuotaMapper employeeLeaveQuotaMapper;

        @Mock
        private EmployeeLeaveHistoryMapper employeeLeaveHistoryMapper;

        @InjectMocks
        private LeaveManagementServiceImpl leaveManagementService;

        @Test
        void assignOrUpdateQuota_createsNewQuota_whenNoExistingRecord() throws Exception {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setActive(true);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
                when(employeeLeaveQuotaRepository.findByEmployee_IdAndLeaveType_IdAndYear(4L, 1L, 2026))
                                .thenReturn(Optional.empty());
                when(employeeLeaveQuotaRepository.save(any(EmployeeLeaveQuota.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(employeeLeaveQuotaMapper.toDTO(any(EmployeeLeaveQuota.class))).thenAnswer(inv -> {
                        EmployeeLeaveQuota q = inv.getArgument(0);
                        EmployeeLeaveQuotaDTO d = new EmployeeLeaveQuotaDTO();
                        d.setEmployeeId(q.getEmployee().getId());
                        d.setLeaveTypeId(q.getLeaveType().getId());
                        d.setYear(q.getYear());
                        d.setQuotaDays(q.getQuotaDays());
                        return d;
                });

                EmployeeLeaveQuotaDTO saved = leaveManagementService.assignOrUpdateQuota(4L, 1L, 2026, 12);

                assertEquals(4L, saved.getEmployeeId());
                assertEquals(1L, saved.getLeaveTypeId());
                assertEquals(2026, saved.getYear());
                assertEquals(12, saved.getQuotaDays());
                verify(employeeLeaveQuotaRepository).save(any(EmployeeLeaveQuota.class));
        }

        @Test
        void assignOrUpdateQuota_updatesExistingQuota_whenRecordExists() throws Exception {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(2L);
                leaveType.setActive(true);

                EmployeeLeaveQuota existing = new EmployeeLeaveQuota();
                existing.setId(101L);
                existing.setEmployee(employee);
                existing.setLeaveType(leaveType);
                existing.setYear(2026);
                existing.setQuotaDays(8);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(2L)).thenReturn(Optional.of(leaveType));
                when(employeeLeaveQuotaRepository.findByEmployee_IdAndLeaveType_IdAndYear(4L, 2L, 2026))
                                .thenReturn(Optional.of(existing));
                when(employeeLeaveQuotaRepository.save(any(EmployeeLeaveQuota.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(employeeLeaveQuotaMapper.toDTO(any(EmployeeLeaveQuota.class))).thenAnswer(inv -> {
                        EmployeeLeaveQuota q = inv.getArgument(0);
                        EmployeeLeaveQuotaDTO d = new EmployeeLeaveQuotaDTO();
                        d.setId(q.getId());
                        d.setQuotaDays(q.getQuotaDays());
                        return d;
                });

                EmployeeLeaveQuotaDTO saved = leaveManagementService.assignOrUpdateQuota(4L, 2L, 2026, 15);

                assertEquals(101L, saved.getId());
                assertEquals(15, saved.getQuotaDays());
                verify(employeeLeaveQuotaRepository).save(existing);
        }

        @Test
        void assignOrUpdateQuota_throwsWhenEmployeeIsInactive() {
                Employee inactiveEmployee = new Employee();
                inactiveEmployee.setId(4L);
                inactiveEmployee.setActive(false);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setActive(true);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(inactiveEmployee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

                Exception ex = assertThrows(Exception.class,
                                () -> leaveManagementService.assignOrUpdateQuota(4L, 1L, 2026, 10));

                assertTrue(ex.getMessage().contains("inactive employee"));
        }

        @Test
        void assignOrUpdateQuota_throwsWhenLeaveTypeIsInactive() {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType inactiveType = new LeaveType();
                inactiveType.setId(1L);
                inactiveType.setActive(false);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(inactiveType));

                Exception ex = assertThrows(Exception.class,
                                () -> leaveManagementService.assignOrUpdateQuota(4L, 1L, 2026, 10));

                assertTrue(ex.getMessage().contains("inactive leave type"));
        }

        @Test
        void assignOrUpdateQuota_throwsWhenQuotaDaysExceedMax() {
                Exception ex = assertThrows(Exception.class,
                                () -> leaveManagementService.assignOrUpdateQuota(4L, 1L, 2026, 367));

                assertTrue(ex.getMessage().contains("cannot exceed 366"));
        }

        @Test
        void submitLeaveRequest_throwsWhenStartDateInPast() {
                LocalDate startDate = LocalDate.now().minusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(1);

                Exception ex = assertThrows(Exception.class, () -> leaveManagementService.submitLeaveRequest(
                                4L,
                                1L,
                                startDate,
                                endDate,
                                "Vacation plan"));

                assertTrue(ex.getMessage().contains("Start date cannot be a past date"));
        }

        @Test
        void submitLeaveRequest_throwsWhenReasonIsMissing() {
                LocalDate startDate = LocalDate.now().plusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(2);

                Exception ex = assertThrows(Exception.class, () -> leaveManagementService.submitLeaveRequest(
                                4L,
                                1L,
                                startDate,
                                endDate,
                                "   "));

                assertTrue(ex.getMessage().contains("Reason is required"));
        }

        @Test
        void submitLeaveRequest_throwsWhenOverlappingLeaveExists() {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setActive(true);

                LocalDate startDate = LocalDate.now().plusDays(5);
                LocalDate endDate = LocalDate.now().plusDays(7);

                EmployeeLeaveHistory overlap = new EmployeeLeaveHistory();
                overlap.setEmployee(employee);
                overlap.setLeaveType(leaveType);
                overlap.setStatus("PENDING");
                overlap.setStartDate(LocalDate.now().plusDays(6));
                overlap.setEndDate(LocalDate.now().plusDays(8));

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
                when(employeeLeaveHistoryRepository.findOverlappingActiveLeaveRequests(4L, startDate, endDate))
                                .thenReturn(List.of(overlap));

                Exception ex = assertThrows(Exception.class, () -> leaveManagementService.submitLeaveRequest(
                                4L,
                                1L,
                                startDate,
                                endDate,
                                "Medical appointment"));

                assertTrue(ex.getMessage().contains("overlaps"));
        }

        @Test
        void submitLeaveRequest_throwsWhenLeaveBalanceIsInsufficient() {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setActive(true);

                EmployeeLeaveQuota quota = new EmployeeLeaveQuota();
                quota.setEmployee(employee);
                quota.setLeaveType(leaveType);
                quota.setQuotaDays(2);

                LocalDate startDate = LocalDate.now().plusDays(10);
                LocalDate endDate = startDate.plusDays(2);
                int requestYear = startDate.getYear();
                quota.setYear(requestYear);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
                when(employeeLeaveHistoryRepository.findOverlappingActiveLeaveRequests(4L, startDate, endDate))
                                .thenReturn(List.of());
                when(employeeLeaveQuotaRepository.findByEmployee_IdAndLeaveType_IdAndYear(4L, 1L, requestYear))
                                .thenReturn(Optional.of(quota));
                when(employeeLeaveHistoryRepository.sumApprovedLeaveDaysByEmployeeAndLeaveTypeAndYear(4L, 1L,
                                requestYear))
                                .thenReturn(0);

                Exception ex = assertThrows(Exception.class, () -> leaveManagementService.submitLeaveRequest(
                                4L,
                                1L,
                                startDate,
                                endDate,
                                "Family function"));

                assertTrue(ex.getMessage().contains("Insufficient leave balance"));
        }

        @Test
        void submitLeaveRequest_savesPendingRequestWhenValid() throws Exception {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setActive(true);

                EmployeeLeaveQuota quota = new EmployeeLeaveQuota();
                quota.setEmployee(employee);
                quota.setLeaveType(leaveType);
                quota.setQuotaDays(10);

                LocalDate startDate = LocalDate.now().plusDays(20);
                LocalDate endDate = startDate.plusDays(2);
                int requestYear = startDate.getYear();
                quota.setYear(requestYear);

                when(employeeRepository.findById(4L)).thenReturn(Optional.of(employee));
                when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
                when(employeeLeaveHistoryRepository.findOverlappingActiveLeaveRequests(4L, startDate, endDate))
                                .thenReturn(List.of());
                when(employeeLeaveQuotaRepository.findByEmployee_IdAndLeaveType_IdAndYear(4L, 1L, requestYear))
                                .thenReturn(Optional.of(quota));
                when(employeeLeaveHistoryRepository.sumApprovedLeaveDaysByEmployeeAndLeaveTypeAndYear(4L, 1L,
                                requestYear))
                                .thenReturn(3);
                when(employeeLeaveHistoryRepository.save(any(EmployeeLeaveHistory.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(employeeLeaveHistoryMapper.toDTO(any(EmployeeLeaveHistory.class))).thenAnswer(inv -> {
                        EmployeeLeaveHistory h = inv.getArgument(0);
                        EmployeeLeaveHistoryDTO d = new EmployeeLeaveHistoryDTO();
                        d.setStatus(h.getStatus());
                        d.setLeaveDays(h.getLeaveDays());
                        d.setReason(h.getReason());
                        d.setYear(h.getYear());
                        return d;
                });

                EmployeeLeaveHistoryDTO saved = leaveManagementService.submitLeaveRequest(
                                4L,
                                1L,
                                startDate,
                                endDate,
                                "Attending family event");

                assertEquals("PENDING", saved.getStatus());
                assertEquals(3, saved.getLeaveDays());
                assertEquals("Attending family event", saved.getReason());
                assertEquals(requestYear, saved.getYear());
                verify(employeeLeaveHistoryRepository).save(any(EmployeeLeaveHistory.class));
        }

        @Test
        void getEmployeeLeaveApplications_returnsOnlyEmployeeRows() {
                EmployeeLeaveHistory row1 = new EmployeeLeaveHistory();
                row1.setStatus("PENDING");

                EmployeeLeaveHistory row2 = new EmployeeLeaveHistory();
                row2.setStatus("APPROVED");

                when(employeeLeaveHistoryRepository.findByEmployee_IdOrderByAppliedOnDescStartDateDesc(4L))
                                .thenReturn(List.of(row1, row2));
                when(employeeLeaveHistoryMapper.toDTO(row1)).thenAnswer(inv -> {
                        EmployeeLeaveHistoryDTO d = new EmployeeLeaveHistoryDTO();
                        d.setStatus("PENDING");
                        return d;
                });
                when(employeeLeaveHistoryMapper.toDTO(row2)).thenAnswer(inv -> {
                        EmployeeLeaveHistoryDTO d = new EmployeeLeaveHistoryDTO();
                        d.setStatus("APPROVED");
                        return d;
                });

                List<EmployeeLeaveHistoryDTO> result = leaveManagementService.getEmployeeLeaveApplications(4L);

                assertEquals(2, result.size());
                assertEquals("PENDING", result.get(0).getStatus());
                assertEquals("APPROVED", result.get(1).getStatus());
        }

        @Test
        void cancelEmployeeLeaveRequest_throwsWhenStatusIsNotPending() {
                Employee employee = new Employee();
                employee.setRole("EMPLOYEE");

                EmployeeLeaveHistory leave = new EmployeeLeaveHistory();
                leave.setId(88L);
                leave.setStatus("APPROVED");
                leave.setStartDate(LocalDate.now().plusDays(3));
                leave.setEmployee(employee);

                when(employeeLeaveHistoryRepository.findByIdAndEmployee_Id(88L, 4L))
                                .thenReturn(Optional.of(leave));

                Exception ex = assertThrows(Exception.class,
                                () -> leaveManagementService.cancelEmployeeLeaveRequest(4L, 88L));

                assertTrue(ex.getMessage().contains("Only pending leave requests can be cancelled"));
        }

        @Test
        void cancelEmployeeLeaveRequest_throwsWhenLeaveAlreadyStarted() {
                Employee employee = new Employee();
                employee.setRole("EMPLOYEE");

                EmployeeLeaveHistory leave = new EmployeeLeaveHistory();
                leave.setId(89L);
                leave.setStatus("PENDING");
                leave.setStartDate(LocalDate.now());
                leave.setEmployee(employee);

                when(employeeLeaveHistoryRepository.findByIdAndEmployee_Id(89L, 4L))
                                .thenReturn(Optional.of(leave));

                Exception ex = assertThrows(Exception.class,
                                () -> leaveManagementService.cancelEmployeeLeaveRequest(4L, 89L));

                assertTrue(ex.getMessage().contains("already started"));
        }

        @Test
        void cancelEmployeeLeaveRequest_marksCancelledWhenValid() throws Exception {
                Employee employee = new Employee();
                employee.setRole("EMPLOYEE");

                EmployeeLeaveHistory leave = new EmployeeLeaveHistory();
                leave.setId(90L);
                leave.setStatus("PENDING");
                leave.setStartDate(LocalDate.now().plusDays(2));
                leave.setEmployee(employee);

                when(employeeLeaveHistoryRepository.findByIdAndEmployee_Id(90L, 4L))
                                .thenReturn(Optional.of(leave));
                when(employeeLeaveHistoryRepository.save(any(EmployeeLeaveHistory.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(employeeLeaveHistoryMapper.toDTO(any(EmployeeLeaveHistory.class))).thenAnswer(inv -> {
                        EmployeeLeaveHistory h = inv.getArgument(0);
                        EmployeeLeaveHistoryDTO d = new EmployeeLeaveHistoryDTO();
                        d.setStatus(h.getStatus());
                        return d;
                });

                EmployeeLeaveHistoryDTO updated = leaveManagementService.cancelEmployeeLeaveRequest(4L, 90L);

                assertEquals("CANCELLED", updated.getStatus());
                verify(employeeLeaveHistoryRepository).save(leave);
        }

        @Test
        void getEmployeeLeaveBalanceByYear_calculatesQuotaUsedAndBalance() {
                Employee employee = new Employee();
                employee.setId(4L);
                employee.setFirstName("Srinivas");
                employee.setActive(true);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setName("Casual Leave");
                leaveType.setActive(true);

                EmployeeLeaveQuota quota = new EmployeeLeaveQuota();
                quota.setEmployee(employee);
                quota.setLeaveType(leaveType);
                quota.setYear(2026);
                quota.setQuotaDays(12);

                EmployeeLeaveHistory approvedHistory = new EmployeeLeaveHistory();
                approvedHistory.setEmployee(employee);
                approvedHistory.setLeaveType(leaveType);
                approvedHistory.setYear(2026);
                approvedHistory.setStatus("APPROVED");
                approvedHistory.setLeaveDays(4);
                approvedHistory.setStartDate(LocalDate.of(2026, 1, 10));
                approvedHistory.setEndDate(LocalDate.of(2026, 1, 13));
                approvedHistory.setAppliedOn(LocalDate.of(2026, 1, 5));

                when(employeeLeaveQuotaRepository.findByYear(2026)).thenReturn(List.of(quota));
                when(employeeLeaveHistoryRepository.findByYearAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(2026,
                                "APPROVED"))
                                .thenReturn(List.of(approvedHistory));

                List<EmployeeLeaveBalanceSummary> balances = leaveManagementService.getEmployeeLeaveBalanceByYear(2026);

                assertEquals(1, balances.size());
                assertEquals(12, balances.get(0).getQuotaDays());
                assertEquals(4, balances.get(0).getUsedDays());
                assertEquals(8, balances.get(0).getBalanceDays());
        }

        @Test
        void getLeaveHistoryByYear_returnsRecordsForRequestedYear() {
                EmployeeLeaveHistory history = new EmployeeLeaveHistory();
                history.setYear(2026);
                history.setStatus("APPROVED");
                history.setLeaveDays(2);

                when(employeeLeaveHistoryRepository.findByYearOrderByAppliedOnDescStartDateDesc(2026))
                                .thenReturn(List.of(history));
                when(employeeLeaveHistoryMapper.toDTO(history)).thenAnswer(inv -> {
                        EmployeeLeaveHistoryDTO d = new EmployeeLeaveHistoryDTO();
                        d.setYear(2026);
                        d.setStatus("APPROVED");
                        d.setLeaveDays(2);
                        return d;
                });

                List<EmployeeLeaveHistoryDTO> result = leaveManagementService.getLeaveHistoryByYear(2026);

                assertEquals(1, result.size());
                assertEquals("APPROVED", result.get(0).getStatus());
                assertEquals(2, result.get(0).getLeaveDays());
        }

        @Test
        void getEmployeeWiseLeaveReport_aggregatesByEmployee() {
                Employee emp1 = new Employee();
                emp1.setId(4L);
                emp1.setFirstName("Srinivas");
                emp1.setDepartment("Engineering");

                Employee emp2 = new Employee();
                emp2.setId(5L);
                emp2.setFirstName("Venu");
                emp2.setDepartment(null);

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setName("Casual Leave");

                EmployeeLeaveQuota quota1 = new EmployeeLeaveQuota();
                quota1.setEmployee(emp1);
                quota1.setLeaveType(leaveType);
                quota1.setYear(2026);
                quota1.setQuotaDays(12);

                EmployeeLeaveQuota quota2 = new EmployeeLeaveQuota();
                quota2.setEmployee(emp2);
                quota2.setLeaveType(leaveType);
                quota2.setYear(2026);
                quota2.setQuotaDays(8);

                EmployeeLeaveHistory h1 = new EmployeeLeaveHistory();
                h1.setEmployee(emp1);
                h1.setLeaveType(leaveType);
                h1.setYear(2026);
                h1.setStatus("APPROVED");
                h1.setLeaveDays(3);

                EmployeeLeaveHistory h2 = new EmployeeLeaveHistory();
                h2.setEmployee(emp1);
                h2.setLeaveType(leaveType);
                h2.setYear(2026);
                h2.setStatus("PENDING");
                h2.setLeaveDays(2);

                EmployeeLeaveHistory h3 = new EmployeeLeaveHistory();
                h3.setEmployee(emp2);
                h3.setLeaveType(leaveType);
                h3.setYear(2026);
                h3.setStatus("REJECTED");
                h3.setLeaveDays(1);

                when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));
                when(employeeLeaveQuotaRepository.findByYear(2026)).thenReturn(List.of(quota1, quota2));
                when(employeeLeaveHistoryRepository.findByYearOrderByAppliedOnDescStartDateDesc(2026))
                                .thenReturn(List.of(h1, h2, h3));

                List<EmployeeLeaveReportSummary> report = leaveManagementService.getEmployeeWiseLeaveReport(2026);

                assertEquals(2, report.size());

                EmployeeLeaveReportSummary emp1Row = report.stream()
                                .filter(r -> r.getEmployeeId().equals(4L))
                                .findFirst()
                                .orElseThrow();
                assertEquals("Engineering", emp1Row.getDepartmentName());
                assertEquals(12, emp1Row.getQuotaDays());
                assertEquals(3, emp1Row.getUsedDays());
                assertEquals(9, emp1Row.getBalanceDays());
                assertEquals(2, emp1Row.getTotalRequests());
                assertEquals(1, emp1Row.getApprovedRequests());
                assertEquals(1, emp1Row.getPendingRequests());
                assertEquals(0, emp1Row.getRejectedRequests());

                EmployeeLeaveReportSummary emp2Row = report.stream()
                                .filter(r -> r.getEmployeeId().equals(5L))
                                .findFirst()
                                .orElseThrow();
                assertEquals("Unassigned", emp2Row.getDepartmentName());
                assertEquals(8, emp2Row.getQuotaDays());
                assertEquals(0, emp2Row.getUsedDays());
                assertEquals(8, emp2Row.getBalanceDays());
                assertEquals(1, emp2Row.getTotalRequests());
                assertEquals(0, emp2Row.getApprovedRequests());
                assertEquals(0, emp2Row.getPendingRequests());
                assertEquals(1, emp2Row.getRejectedRequests());
        }

        @Test
        void getDepartmentWiseLeaveReport_aggregatesByDepartment() {
                Employee emp1 = new Employee();
                emp1.setId(4L);
                emp1.setFirstName("Srinivas");
                emp1.setDepartment("Engineering");

                Employee emp2 = new Employee();
                emp2.setId(5L);
                emp2.setFirstName("Venu");
                emp2.setDepartment("Engineering");

                LeaveType leaveType = new LeaveType();
                leaveType.setId(1L);
                leaveType.setName("Casual Leave");

                EmployeeLeaveQuota quota1 = new EmployeeLeaveQuota();
                quota1.setEmployee(emp1);
                quota1.setLeaveType(leaveType);
                quota1.setYear(2026);
                quota1.setQuotaDays(12);

                EmployeeLeaveQuota quota2 = new EmployeeLeaveQuota();
                quota2.setEmployee(emp2);
                quota2.setLeaveType(leaveType);
                quota2.setYear(2026);
                quota2.setQuotaDays(8);

                EmployeeLeaveHistory h1 = new EmployeeLeaveHistory();
                h1.setEmployee(emp1);
                h1.setLeaveType(leaveType);
                h1.setYear(2026);
                h1.setStatus("APPROVED");
                h1.setLeaveDays(2);

                EmployeeLeaveHistory h2 = new EmployeeLeaveHistory();
                h2.setEmployee(emp2);
                h2.setLeaveType(leaveType);
                h2.setYear(2026);
                h2.setStatus("PENDING");
                h2.setLeaveDays(1);

                when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));
                when(employeeLeaveQuotaRepository.findByYear(2026)).thenReturn(List.of(quota1, quota2));
                when(employeeLeaveHistoryRepository.findByYearOrderByAppliedOnDescStartDateDesc(2026))
                                .thenReturn(List.of(h1, h2));

                List<DepartmentLeaveReportSummary> report = leaveManagementService.getDepartmentWiseLeaveReport(2026);

                assertEquals(1, report.size());
                DepartmentLeaveReportSummary engineering = report.get(0);
                assertEquals("Engineering", engineering.getDepartmentName());
                assertEquals(2, engineering.getEmployeeCount());
                assertEquals(20, engineering.getTotalQuotaDays());
                assertEquals(2, engineering.getTotalUsedDays());
                assertEquals(18, engineering.getTotalBalanceDays());
                assertEquals(2, engineering.getTotalRequests());
                assertEquals(1, engineering.getApprovedRequests());
                assertEquals(1, engineering.getPendingRequests());
                assertEquals(0, engineering.getRejectedRequests());
        }
}
