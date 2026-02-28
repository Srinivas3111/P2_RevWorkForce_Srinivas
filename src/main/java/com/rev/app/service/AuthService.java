package com.rev.app.service;

import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee authenticate(String identifier, String password) {
        if (identifier == null || password == null)
            return null;

        String cleanId = identifier.trim();
        String cleanPass = password.trim();

        // 1. Try finding by Case-Insensitive Employee ID (e.g., "1", "4")
        Optional<Employee> employeeOpt = employeeRepository.findByEmployeeIdIgnoreCase(cleanId);

        // 2. If not found, try finding by Case-Insensitive Email
        if (employeeOpt.isEmpty()) {
            employeeOpt = employeeRepository.findByEmailIgnoreCase(cleanId);
        }

        // 3. Fallback: try finding by database Primary Key (Long id) if input is
        // numeric
        if (employeeOpt.isEmpty()) {
            try {
                Long id = Long.parseLong(cleanId);
                employeeOpt = employeeRepository.findById(id);
            } catch (NumberFormatException e) {
                // Not a number, skip
            }
        }

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            // Case-sensitive exact password check
            if (employee.getPassword().equals(cleanPass)) {
                return employee;
            }
        }
        return null;
    }
}
