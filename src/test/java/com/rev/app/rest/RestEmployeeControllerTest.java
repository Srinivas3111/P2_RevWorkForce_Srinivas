package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.EmployeeDTO;
import com.rev.app.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestEmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private RestEmployeeController restEmployeeController;

    @Test
    void getAllEmployees_returnsSuccessResponse() {
        EmployeeDTO e1 = new EmployeeDTO();
        e1.setId(1L);
        e1.setFirstName("Anita");
        when(employeeService.getAllEmployees()).thenReturn(List.of(e1));

        ResponseEntity<ApiResponse<List<EmployeeDTO>>> response = restEmployeeController.getAllEmployees();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void getEmployeeById_returnsNotFoundWhenEmployeeMissing() {
        when(employeeService.getEmployeeDTOById(77L)).thenReturn(null);

        ResponseEntity<ApiResponse<EmployeeDTO>> response = restEmployeeController.getEmployeeById(77L);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Employee not found", response.getBody().getMessage());
    }

    @Test
    void searchEmployees_returnsSuccessWithMatchedEmployees() {
        EmployeeDTO e1 = new EmployeeDTO();
        e1.setId(10L);
        e1.setFirstName("Rahul");
        when(employeeService.searchDirectoryEmployees("rah")).thenReturn(List.of(e1));

        ResponseEntity<ApiResponse<List<EmployeeDTO>>> response = restEmployeeController.searchEmployees("rah");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
    }
}

