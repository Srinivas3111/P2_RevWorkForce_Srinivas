package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Integer> {

    List<Goal> findByEmployee(Employee employee);

    List<Goal> findByEmployeeAndStatus(Employee employee, String status);

    List<Goal> findByEmployeeIn(List<Employee> employees);
}
