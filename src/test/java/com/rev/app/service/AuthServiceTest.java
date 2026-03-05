package com.rev.app.service;

import com.rev.app.service.impl.AuthServiceImpl;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.EmployeeMapper;
import com.rev.app.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_acceptsPlainPassword() {
        Employee employee = buildEmployee(10L, "Secure@123");

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDTO(employee)).thenReturn(new EmployeeDTO());

        EmployeeDTO authenticated = authService.authenticate("10", "Secure@123");

        assertNotNull(authenticated);
    }

    @Test
    void authenticate_rejectsWrongPassword() {
        Employee employee = buildEmployee(12L, "Correct@123");

        when(employeeRepository.findById(12L)).thenReturn(Optional.of(employee));

        EmployeeDTO authenticated = authService.authenticate("12", "WrongPassword");

        assertNull(authenticated);
    }

    private Employee buildEmployee(Long id, String storedPassword) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmail("employee" + id + "@revworkforce.com");
        employee.setPassword(storedPassword);
        employee.setRole("EMPLOYEE");
        employee.setActive(true);
        return employee;
    }
}


