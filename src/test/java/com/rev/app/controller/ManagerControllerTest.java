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

@WebMvcTest(ManagerController.class)
@Import(TestSecurityConfig.class)
class ManagerControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private EmployeeUserDetailsService employeeUserDetailsService;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private LeaveManagementService leaveManagementService;

    @MockitoBean
    private PerformanceReviewService performanceReviewService;

    @MockitoBean
    private PerformanceGoalService performanceGoalService;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @MockitoBean
    private AnnouncementService announcementService;

    @MockitoBean
    private SystemActivityLogService systemActivityLogService;

    @Test
    void contextLoads() {
    }
}
