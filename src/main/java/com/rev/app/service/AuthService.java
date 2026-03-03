package com.rev.app.service;

import com.rev.app.dto.EmployeeDTO;

public interface AuthService {
    EmployeeDTO authenticate(String identifier, String password);

<<<<<<< HEAD
    boolean isInactiveAccount(String identifier, String password);
=======
@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee authenticate(String identifier, String password) {
        if (identifier == null || password == null)
            return null;

        String cleanId = identifier.trim();
        String cleanPass = password.trim();

        Optional<Employee> employeeOpt = Optional.empty();

        // 1. Try finding by database Primary Key (Long id) if input is numeric
        try {
            Long id = Long.parseLong(cleanId);
            employeeOpt = employeeRepository.findById(id);
        } catch (NumberFormatException e) {
            // Not a number, skip to email search
        } catch (org.springframework.dao.DataAccessException | jakarta.persistence.PersistenceException e) {
            // Database-level numeric conversion error (e.g. ORA-01722)
            // Fallback to email search
        }

        // 2. If not found by ID, try finding by Case-Insensitive Email
        if (employeeOpt.isEmpty()) {
            employeeOpt = employeeRepository.findByEmailIgnoreCase(cleanId);
        }

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            // Case-sensitive exact password check
            if (employee.getPassword().equals(cleanPass)) {
                return employee;
            } else {
            }
        } else {
        }
        return null;
    }
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
}
