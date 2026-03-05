package com.rev.app.service;

import com.rev.app.service.impl.EmployeeServiceImpl;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.EmployeeMapper;
import com.rev.app.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeMapper employeeMapper;

    @Test
    void searchDirectoryEmployees_returnsAllActiveWhenKeywordEmpty() {
        Employee emp1 = buildEmployee(102L, "Anita", "Engineering", "Developer");
        Employee emp2 = buildEmployee(101L, "Rahul", "QA", "Test Engineer");

        when(employeeRepository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(emp1, emp2));
        // Mocking mapper because it is used in the service
        when(employeeMapper.toDTO(emp1)).thenReturn(buildEmployeeDTO(emp1));
        when(employeeMapper.toDTO(emp2)).thenReturn(buildEmployeeDTO(emp2));

        List<EmployeeDTO> result = employeeService.searchDirectoryEmployees("   ");

        assertEquals(2, result.size());
        assertEquals(102L, result.get(0).getId());
        assertEquals(101L, result.get(1).getId());
    }

    @Test
    void searchDirectoryEmployees_filtersBySupportedFields() {
        Employee emp1 = buildEmployee(102L, "Anita", "Engineering", "Developer");
        Employee emp2 = buildEmployee(101L, "Rahul", "Quality", "Test Engineer");
        Employee emp3 = buildEmployee(201L, "Meera", "Finance", "Analyst");

        when(employeeRepository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(emp1, emp2, emp3));
        when(employeeMapper.toDTO(emp1)).thenReturn(buildEmployeeDTO(emp1));
        when(employeeMapper.toDTO(emp2)).thenReturn(buildEmployeeDTO(emp2));
        when(employeeMapper.toDTO(emp3)).thenReturn(buildEmployeeDTO(emp3));

        assertEquals(1, employeeService.searchDirectoryEmployees("anita").size());
        assertEquals(1, employeeService.searchDirectoryEmployees("201").size());
        assertEquals(1, employeeService.searchDirectoryEmployees("finance").size());
        assertEquals(1, employeeService.searchDirectoryEmployees("test engineer").size());
        assertTrue(employeeService.searchDirectoryEmployees("non-existent").isEmpty());
    }

    @Test
    void saveEmployee_rejectsInvalidRole() {
        Employee invalidEmployee = buildEmployee(999L, "Test", "IT", "INVALID_ROLE");
        invalidEmployee.setRole("INVALID_ROLE");
        EmployeeDTO employeeDTO = buildEmployeeDTO(invalidEmployee);
        when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenReturn(invalidEmployee);

        Exception ex = assertThrows(Exception.class, () -> employeeService.saveEmployee(employeeDTO));
        assertTrue(ex.getMessage().contains("Role must be ADMIN, MANAGER, or EMPLOYEE"));
    }

    @Test
    void saveEmployee_keepsExistingPasswordWhenBlankOnUpdate() throws Exception {
        Employee existing = buildEmployee(302L, "Ravi", "IT", "Manager");
        existing.setEmail("ravi.kumar@revworkforce.com");
        existing.setPhoneNumber("9999999999");
        existing.setSalary(90000.0);
        existing.setRole("MANAGER");
        existing.setPassword("$2a$10$abcdefghijklmnopqrstuv");
        existing.setActive(true);

        Employee updateRequestEntity = buildEmployee(302L, "Ravi", "IT", "Manager");
        updateRequestEntity.setEmail("ravi.kumar@revworkforce.com");
        updateRequestEntity.setPhoneNumber("9999999999");
        updateRequestEntity.setSalary(91000.0);
        updateRequestEntity.setRole("MANAGER");
        updateRequestEntity.setPassword("   ");
        updateRequestEntity.setActive(true);

        EmployeeDTO updateRequest = buildEmployeeDTO(updateRequestEntity);
        updateRequest.setPassword("   ");

        when(employeeRepository.findById(302L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findByEmailIgnoreCase("ravi.kumar@revworkforce.com")).thenReturn(Optional.of(existing));
        when(employeeRepository.existsById(302L)).thenReturn(true);
        when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenReturn(updateRequestEntity);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeMapper.toDTO(any(Employee.class)))
                .thenAnswer(invocation -> buildEmployeeDTO((Employee) invocation.getArgument(0)));

        EmployeeDTO saved = employeeService.saveEmployee(updateRequest);

        assertNotNull(saved);
        assertEquals(existing.getPassword(), saved.getPassword());
    }

    private Employee buildEmployee(Long id, String firstName, String department, String designation) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setActive(true);
        return employee;
    }

    private EmployeeDTO buildEmployeeDTO(Employee employee) {
        if (employee == null)
            return null;
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setDepartment(employee.getDepartment());
        dto.setDesignation(employee.getDesignation());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setSalary(employee.getSalary());
        dto.setRole(employee.getRole());
        dto.setActive(employee.isActive());
        dto.setPassword(employee.getPassword());
        return dto;
    }
}
