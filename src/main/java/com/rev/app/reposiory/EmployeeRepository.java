package com.rev.app.reposiory;


import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

}