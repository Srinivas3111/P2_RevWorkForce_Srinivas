package com.rev.app.repository;

import com.rev.app.entity.EmployeeLeaveQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeLeaveQuotaRepository extends JpaRepository<EmployeeLeaveQuota, Long> {
    Optional<EmployeeLeaveQuota> findByEmployee_IdAndLeaveType_IdAndYear(Long employeeId, Long leaveTypeId, Integer year);

    List<EmployeeLeaveQuota> findByYear(Integer year);
}
