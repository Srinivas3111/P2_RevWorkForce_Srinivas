package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.JwtAuthResponse;
import com.rev.app.dto.LoginRequest;
import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST endpoint for JWT-based authentication.
 *
 * POST /api/auth/login
 * Body : { "email": "...", "password": "..." }
 * Returns: { "token": "...", "tokenType": "Bearer", "expiresInMs": ..., ... }
 *
 * Clients must include the returned token in subsequent requests:
 * Authorization: Bearer <token>
 */
@RestController
@RequestMapping("/api/auth")
public class RestAuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(
            @RequestBody LoginRequest loginRequest) {

        if (loginRequest == null
                || loginRequest.getEmail() == null
                || loginRequest.getPassword() == null) {
            return ResponseEntity
                    .status(400)
                    .body(ApiResponse.error("Email and password are required"));
        }

        String email = loginRequest.getEmail().trim().toLowerCase();
        String password = loginRequest.getPassword().trim();

        Optional<Employee> employeeOpt = employeeRepository.findByEmailIgnoreCase(email);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();

            if (!employee.isActive()) {
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.error("Account is deactivated. Contact admin."));
            }

            // Plain-text password comparison (matches existing behaviour)
            if (employee.getPassword().equals(password)) {
                String token = jwtUtil.generateToken(
                        employee.getEmail(),
                        employee.getRole(),
                        employee.getId());

                JwtAuthResponse authResponse = new JwtAuthResponse(
                        token,
                        expirationMs,
                        employee.getId(),
                        employee.getEmail(),
                        employee.getRole(),
                        employee.getFirstName());

                return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
            }
        }

        return ResponseEntity
                .status(401)
                .body(ApiResponse.error("Invalid email or password"));
    }
}
