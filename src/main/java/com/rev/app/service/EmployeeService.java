package com.rev.app.service;

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
}
