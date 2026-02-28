package com.rev.app.controller;

import com.rev.app.entity.Employee;
import com.rev.app.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Employee employee = authService.authenticate(email, password);

        if (employee != null) {
            session.setAttribute("loggedInUser", employee);

            // Role-based redirect
            String role = employee.getRole().toUpperCase();
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("MANAGER".equals(role)) {
                return "redirect:/manager/dashboard";
            } else {
                return "redirect:/employee/dashboard";
            }
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "admin_dashboard";
    }

    @GetMapping("/manager/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("loggedInUser");
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "manager_dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("loggedInUser");
        if (user == null || !"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "employee_dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
