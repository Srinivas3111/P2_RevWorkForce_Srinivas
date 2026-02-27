package com.rev.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByDepartmentName(String departmentName);

    boolean existsByDepartmentName(String departmentName);
}