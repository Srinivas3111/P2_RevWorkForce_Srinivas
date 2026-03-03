package com.rev.app.service.impl;
import com.rev.app.service.DesignationService;

import com.rev.app.dto.DesignationDTO;
import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.DesignationMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.service.EmployeeNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DesignationServiceImpl implements DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationMapper designationMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<DesignationDTO> getAllDesignations() {
        return designationRepository.findAllByOrderByNameAsc()
                .stream()
                .map(designationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DesignationDTO> getDesignationsByDepartment(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return List.of();
        }
        return designationRepository.findByDepartmentNameIgnoreCaseOrderByNameAsc(departmentName.trim())
                .stream()
                .map(designationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getEmployeeCountByDesignation(String designationName) {
        if (designationName == null || designationName.trim().isEmpty()) {
            return 0;
        }
        return employeeRepository.countByDesignationIgnoreCase(designationName.trim());
    }

    @Override
    public DesignationDTO createDesignation(String designationName, Long departmentId) throws Exception {
        String cleanName = normalizeName(designationName);

        Department department = null;
        if (departmentId != null) {
            department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new Exception("Department not found"));
        }

        final Department finalDept = department;
        final Long finalDeptId = departmentId;

        // Check if designation already exists in this department
        Optional<Designation> existing = designationRepository.findAllByOrderByNameAsc().stream()
                .filter(d -> d.getName().equalsIgnoreCase(cleanName) &&
                        ((d.getDepartment() == null && finalDept == null) ||
                                (d.getDepartment() != null && finalDept != null
                                        && d.getDepartment().getId().equals(finalDeptId))))
                .findFirst();

        if (existing.isPresent()) {
            throw new Exception("Designation already exists in this department");
        }

        Designation designation = new Designation(cleanName);
        designation.setDepartment(department);
        Designation saved = designationRepository.save(designation);

        String departmentName = saved.getDepartment() != null ? saved.getDepartment().getName() : "General";
        notificationService.createNotificationForRole(
                "ADMIN",
                "Designation Created",
                "Action: Designation Created | Employee: System | Designation: " + saved.getName()
                        + " | Department: " + departmentName);

        return designationMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public DesignationDTO updateDesignation(Long designationId, String newDesignationName, Long departmentId)
            throws Exception {
        if (designationId == null) {
            throw new Exception("Designation id is required");
        }

        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new Exception("Designation not found"));

        Department department = null;
        if (departmentId != null) {
            department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new Exception("Department not found"));
        }

        String cleanName = normalizeName(newDesignationName);

        // Check if designation already exists in this department (excluding current)
        final Department finalDept = department;
        final Long finalDeptId = departmentId;
        Optional<Designation> existing = designationRepository.findAllByOrderByNameAsc().stream()
                .filter(d -> d.getName().equalsIgnoreCase(cleanName) &&
                        !d.getId().equals(designationId) &&
                        ((d.getDepartment() == null && finalDept == null) ||
                                (d.getDepartment() != null && finalDept != null
                                        && d.getDepartment().getId().equals(finalDeptId))))
                .findFirst();

        if (existing.isPresent()) {
            throw new Exception("Designation name already exists in this department");
        }

        String oldName = designation.getName();
        designation.setName(cleanName);
        designation.setDepartment(department);
        Designation savedDesignation = designationRepository.save(designation);

        if (oldName != null && !oldName.equalsIgnoreCase(cleanName)) {
            List<Employee> employees = employeeRepository.findByDesignationIgnoreCase(oldName);
            for (Employee employee : employees) {
                employee.setDesignation(cleanName);
            }
            employeeRepository.saveAll(employees);
        }

        return designationMapper.toDTO(savedDesignation);
    }

    @Override
    public void deleteDesignation(Long designationId) throws Exception {
        if (designationId == null) {
            throw new Exception("Designation id is required");
        }

        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new Exception("Designation not found"));

        long assignedEmployees = getEmployeeCountByDesignation(designation.getName());
        if (assignedEmployees > 0) {
            throw new Exception(
                    "Cannot delete designation. It is assigned to " + assignedEmployees + " employee(s).");
        }

        designationRepository.delete(designation);
    }

    private String normalizeName(String designationName) throws Exception {
        String cleanName = (designationName == null) ? "" : designationName.trim();
        if (cleanName.isEmpty()) {
            throw new Exception("Designation name is required");
        }
        if (cleanName.length() > 120) {
            throw new Exception("Designation name is too long");
        }
        return cleanName;
    }
}

