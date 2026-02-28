package com.rev.app.repository;

import com.rev.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmailIgnoreCase(String email);

    @org.springframework.data.jpa.repository.Query("SELECT MAX(e.id) FROM Employee e")
    Long findMaxId();

    List<Employee> findByRoleIgnoreCase(String role);
}
