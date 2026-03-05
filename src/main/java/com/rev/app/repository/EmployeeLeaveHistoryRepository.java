package com.rev.app.repository;

import com.rev.app.entity.EmployeeLeaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeLeaveHistoryRepository extends JpaRepository<EmployeeLeaveHistory, Long> {

        List<EmployeeLeaveHistory> findByYearOrderByAppliedOnDescStartDateDesc(Integer year);

        List<EmployeeLeaveHistory> findByYearAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(Integer year,
                        String status);

        Optional<EmployeeLeaveHistory> findByEmployee_IdAndLeaveType_IdAndStartDateAndEndDate(
                        Long employeeId,
                        Long leaveTypeId,
                        LocalDate startDate,
                        LocalDate endDate);

        List<EmployeeLeaveHistory> findByEmployee_Manager_IdOrderByAppliedOnDescStartDateDesc(Long managerId);

        List<EmployeeLeaveHistory> findByEmployee_Manager_IdAndStatusIgnoreCaseOrderByAppliedOnDescStartDateDesc(
                        Long managerId,
                        String status);

        List<EmployeeLeaveHistory> findByEmployee_IdOrderByAppliedOnDescStartDateDesc(Long employeeId);

        Optional<EmployeeLeaveHistory> findByIdAndEmployee_Id(Long id, Long employeeId);

        Optional<EmployeeLeaveHistory> findByIdAndEmployee_Manager_Id(Long id, Long managerId);

        @Query("""
                        SELECT COALESCE(SUM(h.leaveDays), 0)
                        FROM EmployeeLeaveHistory h
                        WHERE h.employee.id = :employeeId
                          AND h.leaveType.id = :leaveTypeId
                          AND h.year = :year
                          AND UPPER(h.status) = 'APPROVED'
                        """)
        Integer sumApprovedLeaveDaysByEmployeeAndLeaveTypeAndYear(
                        @Param("employeeId") Long employeeId,
                        @Param("leaveTypeId") Long leaveTypeId,
                        @Param("year") Integer year);

        @Query("""
                        SELECT h
                        FROM EmployeeLeaveHistory h
                        WHERE h.employee.id = :employeeId
                          AND UPPER(h.status) IN ('PENDING', 'APPROVED')
                          AND h.startDate <= :endDate
                          AND h.endDate >= :startDate
                        """)
        List<EmployeeLeaveHistory> findOverlappingActiveLeaveRequests(
                        @Param("employeeId") Long employeeId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("""
                        SELECT h FROM EmployeeLeaveHistory h
                        WHERE UPPER(h.employee.role) = 'MANAGER'
                        ORDER BY h.appliedOn DESC, h.startDate DESC
                        """)
        List<EmployeeLeaveHistory> findAllManagerLeaveRequests();
}
