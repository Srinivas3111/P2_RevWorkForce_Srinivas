package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.DepartmentDTO;
import com.rev.app.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestDepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private RestDepartmentController restDepartmentController;

    @Test
    void createDepartment_returnsSuccessWhenCreationWorks() throws Exception {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(1L);
        dto.setName("Engineering");
        when(departmentService.createDepartment("Engineering")).thenReturn(dto);

        ResponseEntity<ApiResponse<DepartmentDTO>> response =
                restDepartmentController.createDepartment("Engineering");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engineering", response.getBody().getData().getName());
    }

    @Test
    void createDepartment_returnsBadRequestWhenServiceThrows() throws Exception {
        when(departmentService.createDepartment("Engineering"))
                .thenThrow(new Exception("Department already exists"));

        ResponseEntity<ApiResponse<DepartmentDTO>> response =
                restDepartmentController.createDepartment("Engineering");

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Department already exists", response.getBody().getMessage());
    }

    @Test
    void deleteDepartment_returnsBadRequestWhenServiceThrows() throws Exception {
        doThrow(new Exception("Department not found"))
                .when(departmentService)
                .deleteDepartment(99L);

        ResponseEntity<ApiResponse<Void>> response = restDepartmentController.deleteDepartment(99L);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Department not found", response.getBody().getMessage());
    }
}

