package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.EmployeeMapper;
import com.rev.app.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeeDTO authenticate(String identifier, String password) {
        Optional<Employee> employeeOpt = findByIdentifier(identifier);
        if (employeeOpt.isEmpty()) {
            return null;
        }

        Employee employee = employeeOpt.get();
        if (!passwordMatches(employee, password)) {
            LOGGER.debug("Password mismatch for user identifier={}", identifier);
            return null;
        }

        // Note: We return the DTO even if inactive, and let the controller decide
        // whether to block login.
        // This allows the controller to show a specific "Account deactivated" message.
        return employeeMapper.toDTO(employee);
    }

    @Override
    public boolean isInactiveAccount(String identifier, String password) {
        Optional<Employee> employeeOpt = findByIdentifier(identifier);
        if (employeeOpt.isEmpty()) {
            return false;
        }
        Employee employee = employeeOpt.get();
        return passwordMatches(employee, password) && !employee.isActive();
    }

    private Optional<Employee> findByIdentifier(String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }

        String cleanId = identifier.trim();
        if (cleanId.isEmpty()) {
            return Optional.empty();
        }

        Optional<Employee> employeeOpt = Optional.empty();

        // 1. Try by numeric employee ID
        try {
            Long id = Long.parseLong(cleanId);
            employeeOpt = employeeRepository.findById(id);
        } catch (NumberFormatException e) {
            // Not numeric - fallback to email.
        } catch (org.springframework.dao.DataAccessException | jakarta.persistence.PersistenceException e) {
            // On conversion/data errors, fallback to email.
        }

        // 2. If not found by ID, try by exact email
        if (employeeOpt.isEmpty()) {
            employeeOpt = employeeRepository.findByEmailIgnoreCase(cleanId);
        }

        // 3. If not found by exact email, try by handle (if it doesn't contain @)
        if (employeeOpt.isEmpty() && !cleanId.contains("@")) {
            String emailPattern = cleanId.toLowerCase() + "@revworkforce.com";
            employeeOpt = employeeRepository.findByEmailIgnoreCase(emailPattern);
        }

        if (employeeOpt.isEmpty()) {
            LOGGER.debug("User not found in DB for identifier={}", cleanId);
        }

        return employeeOpt;
    }

    private boolean passwordMatches(Employee employee, String password) {
        if (employee == null || employee.getPassword() == null || password == null) {
            return false;
        }
        return employee.getPassword().trim().equals(password.trim());
    }
}



