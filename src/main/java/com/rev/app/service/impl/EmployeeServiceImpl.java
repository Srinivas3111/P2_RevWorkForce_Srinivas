package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.EmployeeMapper;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final List<String> ALLOWED_ROLES = List.of("ADMIN", "MANAGER", "EMPLOYEE");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Employee::getId))
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeRepository.findByActiveTrueOrderByIdAsc()
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> searchDirectoryEmployees(String searchKeyword) {
        List<Employee> activeEmployees = employeeRepository.findByActiveTrueOrderByIdAsc();
        String normalizedKeyword = normalizeSearchKeyword(searchKeyword);
        if (normalizedKeyword.isEmpty()) {
            return activeEmployees.stream()
                    .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(Employee::getId))
                    .map(employeeMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return activeEmployees.stream()
                .filter(employee -> matchesDirectorySearch(employee, normalizedKeyword))
                .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getId))
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> searchManagerDirectory(Long managerId, String searchKeyword) {
        if (managerId == null) {
            return List.of();
        }

        List<Employee> managerReportees = employeeRepository.findByManager_IdOrderByIdAsc(managerId);
        String normalizedKeyword = normalizeSearchKeyword(searchKeyword);

        if (normalizedKeyword.isEmpty()) {
            return managerReportees.stream()
                    .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(Employee::getId))
                    .map(employeeMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return managerReportees.stream()
                .filter(employee -> matchesDirectorySearch(employee, normalizedKeyword))
                .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getId))
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getManagers() {
        return employeeRepository.findByRoleIgnoreCase("MANAGER")
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getActiveDirectReportees(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return employeeRepository.findByManager_IdAndActiveTrueOrderByIdAsc(managerId)
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getAllDirectReportees(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return employeeRepository.findByManager_IdOrderByIdAsc(managerId)
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getActiveDirectReporteeForManager(Long managerId, Long employeeId) throws Exception {
        if (managerId == null) {
            throw new Exception("Manager identity is required.");
        }
        if (employeeId == null) {
            throw new Exception("Employee ID is required.");
        }
        Employee employee = employeeRepository.findByIdAndManager_IdAndActiveTrue(employeeId, managerId)
                .orElseThrow(() -> new Exception("Team member not found in your reporting hierarchy."));
        return employeeMapper.toDTO(employee);
    }

    @Override
    public Long generateUniqueEmployeeId() {
        Long maxId = employeeRepository.findMaxId();
        return (maxId == null) ? 1L : maxId + 1;
    }

    @Override
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) throws Exception {
        Employee employee = employeeMapper.toEntity(employeeDTO);
        validateEmployeeForSave(employee);

        // Resolve reporting manager
        if (employeeDTO.getManagerId() != null) {
            Employee manager = employeeRepository.findById(employeeDTO.getManagerId())
                    .orElseThrow(() -> new Exception("Assigned manager does not exist"));
            employee.setManager(manager);
        }

        Employee existing = (employee.getId() == null) ? null
                : employeeRepository.findById(employee.getId()).orElse(null);

        // Append company domain if it doesn't already have one
        if (employee.getEmail() != null && !employee.getEmail().contains("@")) {
            employee.setEmail(employee.getEmail().toLowerCase() + "@revworkforce.com");
        }

        // 1. Ensure numeric ID is provided or generated
        if (employee.getId() == null) {
            employee.setId(generateUniqueEmployeeId());
        }

        // 2. Ensure Email is unique
        Optional<Employee> existingEmail = employeeRepository.findByEmailIgnoreCase(employee.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(employee.getId())) {
            throw new Exception("Email already exists for another employee");
        }

        // Joining date cannot be future date
        if (employee.getJoiningDate() != null && employee.getJoiningDate().isAfter(LocalDate.now())) {
            throw new Exception("Joining date cannot be in the future");
        }

        // Mandatory reporting manager for EMPLOYEEs
        if ("EMPLOYEE".equalsIgnoreCase(employee.getRole())) {
            if (employee.getManager() == null || employee.getManager().getId() == null) {
                throw new Exception("Reporting manager is required for employees");
            } else {
                // Cannot be own manager
                if (employee.getId() != null && employee.getId().equals(employee.getManager().getId())) {
                    throw new Exception("An employee cannot be their own manager");
                }

                if (!"MANAGER".equalsIgnoreCase(employee.getManager().getRole())) {
                    throw new Exception("Reporting manager must have MANAGER role");
                }
                if (!employee.getManager().isActive()) {
                    throw new Exception("Reporting manager must be active");
                }
            }
        } else {
            employee.setManager(null);
        }

        // Password behavior:
        // - New employee: auto-generate default if blank.
        // - Existing employee: keep old password if blank.
        String cleanPassword = employee.getPassword() == null ? "" : employee.getPassword().trim();
        if (cleanPassword.isEmpty()) {
            if (existing != null) {
                employee.setPassword(existing.getPassword());
            } else {
                employee.setPassword("Rev@123");
            }
        } else {
            if (cleanPassword.length() < 6) {
                throw new Exception("Password must be at least 6 characters");
            }
            employee.setPassword(cleanPassword.trim());
        }

        if (employee.getId() == null || !employeeRepository.existsById(employee.getId())) {
            employee.setActive(true);
        }

        Employee savedEmployee = employeeRepository.save(employee);

        if (existing == null) {
            notificationService.createNotificationForRole(
                    "ADMIN",
                    "Employee Added",
                    "Action: Employee Added | Employee: " + savedEmployee.getName()
                            + " (ID " + savedEmployee.getId() + ") | Role: " + savedEmployee.getRole());
        }

        return employeeMapper.toDTO(savedEmployee);
    }

    @Override
    public EmployeeDTO getEmployeeDTOById(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return employeeMapper.toDTO(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public EmployeeDTO updateOwnContactDetails(Long employeeId,
            String phoneNumber,
            String emergencyContactNumber,
            String address) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee identity is required");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception("Employee not found"));
        if (!employee.isActive()) {
            throw new Exception("Inactive employee profile cannot be updated");
        }

        String cleanPhone = (phoneNumber == null) ? "" : phoneNumber.trim();
        if (!cleanPhone.matches("\\d{10}")) {
            throw new Exception("Phone number must be exactly 10 digits");
        }

        String cleanEmergency = (emergencyContactNumber == null) ? "" : emergencyContactNumber.trim();
        if (!cleanEmergency.isEmpty() && !cleanEmergency.matches("\\d{10}")) {
            throw new Exception("Emergency contact number must be exactly 10 digits");
        }

        String cleanAddress = (address == null) ? "" : address.trim();
        if (cleanAddress.length() > 500) {
            cleanAddress = cleanAddress.substring(0, 500);
        }

        employee.setPhoneNumber(cleanPhone);
        employee.setEmergencyContactNumber(cleanEmergency.isEmpty() ? null : cleanEmergency);
        employee.setAddress(cleanAddress.isEmpty() ? null : cleanAddress);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDTO(saved);
    }

    @Override
    public EmployeeDTO updateEmployeeActiveStatus(Long id, boolean active) throws Exception {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new Exception("Employee not found"));
        employee.setActive(active);
        Employee saved = employeeRepository.save(employee);

        notificationService.createNotificationForRole(
                "ADMIN",
                active ? "Employee Reactivated" : "Employee Deactivated",
                "Action: " + (active ? "Reactivated" : "Deactivated")
                        + " Employee | Employee: " + saved.getName() + " (ID " + saved.getId() + ")");

        return employeeMapper.toDTO(saved);
    }

    @Override
    public EmployeeDTO deactivateEmployee(Long id) throws Exception {
        return updateEmployeeActiveStatus(id, false);
    }

    @Override
    public EmployeeDTO reactivateEmployee(Long id) throws Exception {
        return updateEmployeeActiveStatus(id, true);
    }

    private void validateEmployeeForSave(Employee employee) throws Exception {
        if (employee == null) {
            throw new Exception("Employee data is required");
        }

        String cleanRole = employee.getRole() == null ? "" : employee.getRole().trim().toUpperCase();
        if (!ALLOWED_ROLES.contains(cleanRole)) {
            throw new Exception("Role must be ADMIN, MANAGER, or EMPLOYEE");
        }
        employee.setRole(cleanRole);

        if (employee.getFirstName() == null || !employee.getFirstName().trim().matches("[A-Za-z]+")) {
            throw new Exception("First name must contain only alphabets");
        }

        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new Exception("Email is required");
        }

        if (employee.getPhoneNumber() == null || !employee.getPhoneNumber().trim().matches("\\d{10}")) {
            throw new Exception("Phone number must be exactly 10 digits");
        }

        if (employee.getDepartment() == null || employee.getDepartment().trim().isEmpty()) {
            throw new Exception("Department is required");
        }

        if (employee.getDesignation() == null || employee.getDesignation().trim().isEmpty()) {
            throw new Exception("Designation is required");
        }

        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            throw new Exception("Salary must be a positive number");
        }
    }

    private boolean matchesDirectorySearch(Employee employee, String normalizedKeyword) {
        if (employee == null) {
            return false;
        }

        String employeeId = employee.getId() == null ? "" : String.valueOf(employee.getId());
        String fullName = (employee.getName() == null ? "" : employee.getName()).trim();
        String department = employee.getDepartment() == null ? "" : employee.getDepartment();
        String designation = employee.getDesignation() == null ? "" : employee.getDesignation();

        return containsIgnoreCase(employeeId, normalizedKeyword)
                || containsIgnoreCase(fullName, normalizedKeyword)
                || containsIgnoreCase(department, normalizedKeyword)
                || containsIgnoreCase(designation, normalizedKeyword);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String normalizeSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return "";
        }
        return searchKeyword.trim().toLowerCase();
    }

    @Override
    public void registerEmployee(String firstName, String email, String password) throws Exception {
        // Validate inputs
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new Exception("Full name is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required.");
        }
        if (password == null || password.trim().length() < 6) {
            throw new Exception("Password must be at least 6 characters.");
        }

        // Build the full email (frontend sends username only, add domain if missing)
        String fullEmail = email.trim().toLowerCase();
        if (!fullEmail.contains("@")) {
            fullEmail = fullEmail + "@revworkforce.com";
        }

        // Check for duplicate email
        if (employeeRepository.findByEmailIgnoreCase(fullEmail).isPresent()) {
            throw new Exception("An account with this email already exists.");
        }

        Employee employee = new Employee();
        employee.setId(generateUniqueEmployeeId());
        employee.setFirstName(firstName.trim());
        employee.setEmail(fullEmail);
        employee.setPassword(password.trim());
        employee.setRole("EMPLOYEE");
        employee.setActive(true);
        employee.setPhoneNumber("0000000000"); // placeholder — required NOT NULL
        employee.setDepartment("N/A");
        employee.setDesignation("N/A");
        employee.setJoiningDate(java.time.LocalDate.now());
        employee.setSalary(0.0);

        employeeRepository.save(employee);
    }
}
