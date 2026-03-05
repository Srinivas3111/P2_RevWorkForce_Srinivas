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

@WebMvcTest(EmployeeController.class)
@Import(TestSecurityConfig.class)
class EmployeeControllerTest {

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
    private AnnouncementService announcementService;

    @MockitoBean
    private CompanyHolidayService companyHolidayService;

    @MockitoBean
    private PerformanceReviewService performanceReviewService;

    @MockitoBean
    private PerformanceGoalService performanceGoalService;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @MockitoBean
    private SystemActivityLogService systemActivityLogService;

    @Test
    void contextLoads() {
    }
}
