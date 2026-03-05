package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.EmployeeDTO;
import com.rev.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class RestEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all employees", employees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeDTOById(id);
        if (employee != null) {
            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved employee", employee));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Employee not found"));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getActiveEmployees() {
        List<EmployeeDTO> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved active employees", employees));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> searchEmployees(@RequestParam String keyword) {
        List<EmployeeDTO> employees = employeeService.searchDirectoryEmployees(keyword);
        return ResponseEntity.ok(ApiResponse.success("Successfully searched employees", employees));
    }
}
