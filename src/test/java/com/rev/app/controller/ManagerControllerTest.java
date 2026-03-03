package com.rev.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.rev.app.service.*;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

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
