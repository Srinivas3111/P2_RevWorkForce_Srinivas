package com.rev.app.service;

<<<<<<< HEAD
import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();

    List<EmployeeDTO> getActiveEmployees();

    List<EmployeeDTO> searchDirectoryEmployees(String searchKeyword);

    List<EmployeeDTO> searchManagerDirectory(Long managerId, String searchKeyword);

    List<EmployeeDTO> getManagers();

    List<EmployeeDTO> getActiveDirectReportees(Long managerId);

    List<EmployeeDTO> getAllDirectReportees(Long managerId);

    EmployeeDTO getActiveDirectReporteeForManager(Long managerId, Long employeeId) throws Exception;

    Long generateUniqueEmployeeId();

    EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) throws Exception;

    EmployeeDTO getEmployeeDTOById(Long id);

    Employee getEmployeeById(Long id);

    EmployeeDTO updateOwnContactDetails(Long employeeId,
            String phoneNumber,
            String emergencyContactNumber,
            String address) throws Exception;

    EmployeeDTO updateEmployeeActiveStatus(Long id, boolean active) throws Exception;

    EmployeeDTO deactivateEmployee(Long id) throws Exception;

    EmployeeDTO reactivateEmployee(Long id) throws Exception;

    /**
     * Self-registration from signup page.
     * Saves firstName, email and password into the employees table.
     * Defaults: role=EMPLOYEE, active=true, phone/department/designation = "N/A".
     */
    void registerEmployee(String firstName, String email, String password) throws Exception;
=======
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
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
}
