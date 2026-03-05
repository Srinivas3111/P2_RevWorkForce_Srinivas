package com.rev.app.config;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Comparator;

@Component
@Order(5)
public class DesignationDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesignationDataInitializer.class);

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        try {
            List<Employee> employees = employeeRepository.findAll()
                    .stream()
                    .sorted(Comparator
                            .comparing((Employee emp) -> normalize(emp.getDepartment()), String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(emp -> normalize(emp.getDesignation()), String.CASE_INSENSITIVE_ORDER))
                    .toList();

            for (Employee employee : employees) {
                String departmentName = normalize(employee.getDepartment());
                String designationName = normalize(employee.getDesignation());
                if (departmentName.isEmpty() || designationName.isEmpty()) {
                    continue;
                }

                Department department = departmentRepository.findByNameIgnoreCase(departmentName)
                        .orElseGet(() -> {
                            Department newDepartment = new Department();
                            newDepartment.setName(departmentName);
                            return departmentRepository.save(newDepartment);
                        });

                if (designationRepository
                        .findByNameIgnoreCaseAndDepartmentId(designationName, department.getId())
                        .isPresent()) {
                    continue;
                }

                Designation designation = new Designation();
                designation.setName(designationName);
                designation.setDepartment(department);
                designationRepository.save(designation);
            }
        } catch (Exception e) {
            LOGGER.error("DESIGNATION SEED ERROR: {}", e.getMessage(), e);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
