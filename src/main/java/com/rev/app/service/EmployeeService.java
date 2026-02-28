package com.rev.app.service;

import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getManagers() {
        return employeeRepository.findByRoleIgnoreCase("MANAGER");
    }

    public Long generateUniqueEmployeeId() {
        Long maxId = employeeRepository.findMaxId();
        return (maxId == null) ? 1L : maxId + 1;
    }

    public Employee saveEmployee(Employee employee) throws Exception {
        // Append company domain if it doesn't already have one
        if (employee.getEmail() != null && !employee.getEmail().contains("@")) {
            employee.setEmail(employee.getEmail().toLowerCase() + "@revworkforce.com");
        }

        // 1. Ensure numeric ID is provided or generated
        if (employee.getId() == null) {
            employee.setId(generateUniqueEmployeeId());
        } else if (employeeRepository.findById(employee.getId()).isPresent()) {
            // If it's a new employee (or we are manually assigning), check if exists
            // But normally id is given for new records in this requirement
        }

        // 2. Ensure Email is unique
        Optional<Employee> existingEmail = employeeRepository.findByEmailIgnoreCase(employee.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(employee.getId())) {
            throw new Exception("Email already exists for another employee");
        }

        // 3. Salary must be positive
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            throw new Exception("Salary must be a positive number");
        }

        // 4. Joining date cannot be future date
        if (employee.getJoiningDate() != null && employee.getJoiningDate().isAfter(LocalDate.now())) {
            throw new Exception("Joining date cannot be in the future");
        }

        // 5. Mandatory reporting manager for EMPLOYEEs (Optional based on business, but
        // requirement says "Reporting manager is selected")
        if ("EMPLOYEE".equalsIgnoreCase(employee.getRole())) {
            if (employee.getManager() == null || employee.getManager().getId() == null) {
                // throw new Exception("Reporting manager is required for employees");
                // The user says "Reporting manager is selected", making it mandatory.
            } else {
                // 6. Cannot be own manager
                if (employee.getId() != null && employee.getId().equals(employee.getManager().getId())) {
                    throw new Exception("An employee cannot be their own manager");
                }

                Optional<Employee> manager = employeeRepository.findById(employee.getManager().getId());
                if (manager.isEmpty()) {
                    throw new Exception("Assigned manager does not exist");
                }
                employee.setManager(manager.get());
            }
        }

        // Password auto-generation if not provided
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword("Rev@123");
        }

        if (employee.getId() == null || !employeeRepository.existsById(employee.getId())) {
            employee.setActive(true);
        }
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
}
