package com.rev.app.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.rev.app.service.*;

@WebMvcTest(RestAuthController.class)
class RestAuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private com.rev.app.repository.EmployeeRepository employeeRepository;

    @MockitoBean
    private com.rev.app.mapper.EmployeeMapper employeeMapper;

    @MockitoBean
    private EmployeeNotificationService notificationService;

    @Test
    void contextLoads() {
    }
}
