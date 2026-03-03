package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.DepartmentDTO;
import com.rev.app.entity.Department;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.DepartmentMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllByOrderByNameAsc()
                .stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getEmployeeCountByDepartment(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return 0;
        }
        return employeeRepository.countByDepartmentIgnoreCase(departmentName.trim());
    }

    @Override
    public DepartmentDTO createDepartment(String departmentName) throws Exception {
        String cleanName = normalizeName(departmentName);
        Optional<Department> existing = departmentRepository.findByNameIgnoreCase(cleanName);
        if (existing.isPresent()) {
            throw new Exception("Department already exists");
        }

        Department department = new Department(cleanName);
        Department saved = departmentRepository.save(department);

        notificationService.createNotificationForRole(
                "ADMIN",
                "Department Created",
                "Action: Department Created | Employee: System | Department: " + saved.getName());

        return departmentMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public DepartmentDTO updateDepartment(Long departmentId, String newDepartmentName) throws Exception {
        if (departmentId == null) {
            throw new Exception("Department id is required");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new Exception("Department not found"));

        String cleanName = normalizeName(newDepartmentName);
        Optional<Department> existing = departmentRepository.findByNameIgnoreCase(cleanName);
        if (existing.isPresent() && !existing.get().getId().equals(departmentId)) {
            throw new Exception("Department name already exists");
        }

        String oldName = department.getName();
        department.setName(cleanName);
        Department savedDepartment = departmentRepository.save(department);

        if (oldName != null && !oldName.equalsIgnoreCase(cleanName)) {
            List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase(oldName);
            for (Employee employee : employees) {
                employee.setDepartment(cleanName);
            }
            employeeRepository.saveAll(employees);
        }

        return departmentMapper.toDTO(savedDepartment);
    }

    @Override
    public void deleteDepartment(Long departmentId) throws Exception {
        if (departmentId == null) {
            throw new Exception("Department id is required");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new Exception("Department not found"));

        long assignedEmployees = getEmployeeCountByDepartment(department.getName());
        if (assignedEmployees > 0) {
            throw new Exception("Cannot delete department. It is assigned to " + assignedEmployees + " employee(s).");
        }

        departmentRepository.delete(department);
    }

    private String normalizeName(String departmentName) throws Exception {
        String cleanName = (departmentName == null) ? "" : departmentName.trim();
        if (cleanName.isEmpty()) {
            throw new Exception("Department name is required");
        }
        if (cleanName.length() > 120) {
            throw new Exception("Department name is too long");
        }
        return cleanName;
    }
}



