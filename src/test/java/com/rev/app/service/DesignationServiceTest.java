package com.rev.app.service;

import com.rev.app.dto.DesignationDTO;
import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.mapper.DesignationMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.service.impl.DesignationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesignationServiceTest {

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationMapper designationMapper;

    @Mock
    private EmployeeNotificationService notificationService;

    @InjectMocks
    private DesignationServiceImpl designationService;

    @Test
    void getDesignationsByDepartment_returnsEmptyWhenDepartmentBlank() {
        assertEquals(List.of(), designationService.getDesignationsByDepartment("   "));
    }

    @Test
    void createDesignation_throwsWhenDuplicateExistsInSameDepartment() throws Exception {
        Department department = new Department("Engineering");
        setPrivateField(department, "id", 11L);
        when(departmentRepository.findById(11L)).thenReturn(Optional.of(department));

        Designation existing = new Designation("Developer");
        existing.setDepartment(department);
        when(designationRepository.findAllByOrderByNameAsc()).thenReturn(List.of(existing));

        Exception ex = assertThrows(
                Exception.class,
                () -> designationService.createDesignation("Developer", 11L));

        assertEquals("Designation already exists in this department", ex.getMessage());
    }

    @Test
    void createDesignation_savesAndNotifiesWhenValid() throws Exception {
        Department department = new Department("Engineering");
        setPrivateField(department, "id", 11L);
        when(departmentRepository.findById(11L)).thenReturn(Optional.of(department));
        when(designationRepository.findAllByOrderByNameAsc()).thenReturn(List.of());
        when(designationRepository.save(any(Designation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DesignationDTO dto = new DesignationDTO();
        dto.setName("Developer");
        when(designationMapper.toDTO(any(Designation.class))).thenReturn(dto);

        DesignationDTO created = designationService.createDesignation("  Developer  ", 11L);

        ArgumentCaptor<Designation> designationCaptor = ArgumentCaptor.forClass(Designation.class);
        verify(designationRepository).save(designationCaptor.capture());
        assertEquals("Developer", designationCaptor.getValue().getName());
        assertEquals("Engineering", designationCaptor.getValue().getDepartment().getName());
        assertEquals("Developer", created.getName());
        verify(notificationService).createNotificationForRole(
                eq("ADMIN"),
                eq("Designation Created"),
                contains("Developer"));
    }

    @Test
    void deleteDesignation_throwsWhenDesignationAssignedToEmployees() {
        Designation designation = new Designation("Architect");
        when(designationRepository.findById(2L)).thenReturn(Optional.of(designation));
        when(employeeRepository.countByDesignationIgnoreCase("Architect")).thenReturn(3L);

        Exception ex = assertThrows(Exception.class, () -> designationService.deleteDesignation(2L));

        assertEquals("Cannot delete designation. It is assigned to 3 employee(s).", ex.getMessage());
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

