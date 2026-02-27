package com.rev.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.Employee;
import com.rev.app.entity.Department;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByDepartment(Department department);

    List<Employee> findByManager(Employee manager);

    Optional<Employee> findByUser_UserId(Integer userId);

    List<Employee> findByDesignation_TitleContainingIgnoreCase(String title);

    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}