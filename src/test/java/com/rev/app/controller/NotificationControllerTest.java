package com.rev.app.controller;

import com.rev.app.TestSecurityConfig;
import com.rev.app.security.EmployeeUserDetailsService;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.rev.app.service.*;

@WebMvcTest(NotificationController.class)
@Import(TestSecurityConfig.class)
class NotificationControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private EmployeeUserDetailsService employeeUserDetailsService;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @Test
    void contextLoads() {
    }
}
