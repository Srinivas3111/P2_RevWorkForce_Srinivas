package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.EmployeeDTO;
import com.rev.app.dto.LoginRequest;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.EmployeeMapper;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class RestAuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<EmployeeDTO>> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(400).body(ApiResponse.error("Email and password are required"));
        }

        String email = loginRequest.getEmail().trim().toLowerCase();
        String password = loginRequest.getPassword().trim();

        Optional<Employee> employeeOpt = employeeRepository.findByEmailIgnoreCase(email);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            if (employee.isActive() && employee.getPassword().equals(password)) {
                return ResponseEntity.ok(ApiResponse.success("Login successful", employeeMapper.toDTO(employee)));
            }
        }

        return ResponseEntity.status(401).body(ApiResponse.error("Invalid email or password"));
    }
}
