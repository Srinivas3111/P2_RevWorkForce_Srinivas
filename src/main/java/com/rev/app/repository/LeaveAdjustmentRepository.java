package com.rev.app.repository;

import com.rev.app.entity.LeaveAdjustment;
import com.rev.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveAdjustmentRepository extends JpaRepository<LeaveAdjustment, Integer> {
    List<LeaveAdjustment> findByEmployeeOrderByAdjustedAtDesc(Employee employee);
}
