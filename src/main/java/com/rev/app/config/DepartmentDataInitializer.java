package com.rev.app.config;

import com.rev.app.entity.Department;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class DepartmentDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDataInitializer.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        try {
            List<String> departmentNames = employeeRepository.findAll()
                    .stream()
                    .map(emp -> emp.getDepartment() == null ? "" : emp.getDepartment().trim())
                    .filter(name -> !name.isEmpty())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

            for (String name : departmentNames) {
                if (departmentRepository.findByNameIgnoreCase(name).isPresent()) {
                    continue;
                }
                Department department = new Department();
                department.setName(name);
                departmentRepository.save(department);
            }
        } catch (Exception e) {
            LOGGER.error("DEPARTMENT SEED ERROR: {}", e.getMessage(), e);
        }
    }
}
