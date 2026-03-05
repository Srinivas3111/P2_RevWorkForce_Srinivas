package com.rev.app.service;

import com.rev.app.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> getAllDepartments();

    long getEmployeeCountByDepartment(String departmentName);

    DepartmentDTO createDepartment(String departmentName) throws Exception;

    DepartmentDTO updateDepartment(Long departmentId, String newDepartmentName) throws Exception;

    void deleteDepartment(Long departmentId) throws Exception;
}
