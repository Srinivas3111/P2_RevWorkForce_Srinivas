package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Integer> {

    List<PerformanceReview> findByEmployee(Employee employee);

    Optional<PerformanceReview> findByEmployeeAndYear(Employee employee, Integer year);

    List<PerformanceReview> findByStatus(String status);

    List<PerformanceReview> findByEmployeeIn(List<Employee> employees);
}
