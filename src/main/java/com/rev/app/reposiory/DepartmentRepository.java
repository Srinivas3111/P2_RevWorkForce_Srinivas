package com.rev.app.reposiory;


import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer>{

}