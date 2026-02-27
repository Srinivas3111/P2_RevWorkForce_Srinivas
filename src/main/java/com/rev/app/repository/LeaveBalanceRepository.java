package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveBalance;
import com.rev.app.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {

    List<LeaveBalance> findByEmployee(Employee employee);

    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);
}
