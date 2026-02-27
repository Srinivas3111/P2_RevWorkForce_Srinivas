package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {

    List<LeaveApplication> findByEmployee(Employee employee);

    List<LeaveApplication> findByApprovedBy(Employee employee);

    List<LeaveApplication> findByEmployeeOrderByAppliedOnDesc(Employee employee);

    List<LeaveApplication> findByStatus(String status);

    List<LeaveApplication> findByEmployeeInAndStatus(List<Employee> employees, String status);

    List<LeaveApplication> findByEmployeeIn(List<Employee> employees);

    List<LeaveApplication> findAllByOrderByAppliedOnDesc();
}
