package com.rev.app.controller;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.service.AuthService;
import com.rev.app.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void showLoginPage_returnsLoginWhenNoUserInSession() {
        MockHttpSession session = new MockHttpSession();

        String view = authController.showLoginPage(session);

        assertEquals("login", view);
    }

    @Test
    void showLoginPage_redirectsToDashboardWhenAdminAlreadyLoggedIn() {
        EmployeeDTO admin = new EmployeeDTO();
        admin.setRole("ADMIN");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", admin);

        String view = authController.showLoginPage(session);

        assertEquals("redirect:/admin/dashboard", view);
    }

    @Test
    void login_setsSessionAndRedirectsWhenCredentialsValid() {
        EmployeeDTO manager = new EmployeeDTO();
        manager.setRole("MANAGER");
        manager.setActive(true);
        when(authService.authenticate("manager@revworkforce.com", "pwd123"))
                .thenReturn(manager);

        MockHttpSession session = new MockHttpSession();
        Model model = new ExtendedModelMap();

        String view = authController.login(
                "manager@revworkforce.com",
                "pwd123",
                session,
                new MockHttpServletRequest(),
                model);

        assertEquals("redirect:/manager/dashboard", view);
        assertSame(manager, session.getAttribute("loggedInUser"));
        assertNull(model.asMap().get("error"));
    }

    @Test
    void login_returnsLoginWithErrorWhenAccountInactive() {
        EmployeeDTO inactive = new EmployeeDTO();
        inactive.setRole("EMPLOYEE");
        inactive.setActive(false);
        when(authService.authenticate("employee@revworkforce.com", "pwd123"))
                .thenReturn(inactive);

        MockHttpSession session = new MockHttpSession();
        Model model = new ExtendedModelMap();

        String view = authController.login(
                "employee@revworkforce.com",
                "pwd123",
                session,
                new MockHttpServletRequest(),
                model);

        assertEquals("login", view);
        assertNull(session.getAttribute("loggedInUser"));
        assertEquals("Your account is deactivated. Please contact admin.", model.asMap().get("error"));
    }

    @Test
    void login_returnsLoginWithErrorWhenCredentialsInvalid() {
        when(authService.authenticate("wrong@revworkforce.com", "bad"))
                .thenReturn(null);

        MockHttpSession session = new MockHttpSession();
        Model model = new ExtendedModelMap();

        String view = authController.login(
                "wrong@revworkforce.com",
                "bad",
                session,
                new MockHttpServletRequest(),
                model);

        assertEquals("login", view);
        assertEquals("Invalid email or password", model.asMap().get("error"));
    }
}

