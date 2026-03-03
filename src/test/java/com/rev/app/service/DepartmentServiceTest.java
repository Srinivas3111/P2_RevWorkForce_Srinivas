package com.rev.app.service;

import com.rev.app.dto.DepartmentDTO;
import com.rev.app.entity.Department;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.DepartmentMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private EmployeeNotificationService notificationService;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void createDepartment_throwsWhenDuplicateExists() {
        Department existing = new Department("Engineering");
        when(departmentRepository.findByNameIgnoreCase("Engineering"))
                .thenReturn(Optional.of(existing));

        Exception ex = assertThrows(Exception.class, () -> departmentService.createDepartment("Engineering"));

        assertEquals("Department already exists", ex.getMessage());
    }

    @Test
    void updateDepartment_renamesAndPropagatesToEmployees() throws Exception {
        Department department = new Department("Engineering");
        when(departmentRepository.findById(7L)).thenReturn(Optional.of(department));
        when(departmentRepository.findByNameIgnoreCase("Platform")).thenReturn(Optional.empty());
        when(departmentRepository.save(department)).thenReturn(department);

        Employee employee = new Employee();
        employee.setDepartment("Engineering");
        when(employeeRepository.findByDepartmentIgnoreCase("Engineering")).thenReturn(List.of(employee));

        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Platform");
        when(departmentMapper.toDTO(department)).thenReturn(dto);

        DepartmentDTO updated = departmentService.updateDepartment(7L, "Platform");

        assertEquals("Platform", department.getName());
        assertEquals("Platform", employee.getDepartment());
        assertEquals("Platform", updated.getName());
        verify(employeeRepository).saveAll(anyList());
    }

    @Test
    void deleteDepartment_throwsWhenDepartmentHasAssignedEmployees() {
        Department department = new Department("QA");
        when(departmentRepository.findById(9L)).thenReturn(Optional.of(department));
        when(employeeRepository.countByDepartmentIgnoreCase("QA")).thenReturn(2L);

        Exception ex = assertThrows(Exception.class, () -> departmentService.deleteDepartment(9L));

        assertEquals("Cannot delete department. It is assigned to 2 employee(s).", ex.getMessage());
    }
}

