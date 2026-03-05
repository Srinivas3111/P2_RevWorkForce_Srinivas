package com.rev.app.controller;

import com.rev.app.TestSecurityConfig;
import com.rev.app.security.EmployeeUserDetailsService;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.DepartmentService;
import com.rev.app.service.DesignationService;
import com.rev.app.service.LeaveManagementService;
import com.rev.app.service.CompanyHolidayService;
import com.rev.app.service.AnnouncementService;
import com.rev.app.service.SystemActivityLogService;
import com.rev.app.service.EmployeeNotificationService;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private EmployeeUserDetailsService employeeUserDetailsService;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private DesignationService designationService;

    @MockitoBean
    private LeaveManagementService leaveManagementService;

    @MockitoBean
    private CompanyHolidayService companyHolidayService;

    @MockitoBean
    private AnnouncementService announcementService;

    @MockitoBean
    private SystemActivityLogService systemActivityLogService;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @Test
    void contextLoads() {
    }
}
