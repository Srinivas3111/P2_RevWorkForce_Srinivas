package com.rev.app.rest;

import com.rev.app.TestSecurityConfig;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.security.EmployeeUserDetailsService;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.AuthService;
import com.rev.app.service.EmployeeNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(RestAuthController.class)
@Import(TestSecurityConfig.class)
class RestAuthControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private EmployeeUserDetailsService employeeUserDetailsService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @Test
    void contextLoads() {
    }
}
