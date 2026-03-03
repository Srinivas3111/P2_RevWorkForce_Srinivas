package com.rev.app.controller;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.service.AuthService;
import com.rev.app.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
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
    private EmployeeService employeeService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String showHomePage(HttpSession session) {
        EmployeeDTO user = (EmployeeDTO) session.getAttribute("loggedInUser");
        if (user != null) {
            return redirectBasedOnRole(user);
        }
        return "home";
    }

    @GetMapping("/home")
    public String showHomePageAlias(HttpSession session) {
        return showHomePage(session);
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        EmployeeDTO user = (EmployeeDTO) session.getAttribute("loggedInUser");
        if (user != null) {
            return redirectBasedOnRole(user);
        }
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String firstName,
            @RequestParam String emailUser,
            @RequestParam String password,
            Model model) {
        try {
            // emailUser is the part before @, service appends domain
            employeeService.registerEmployee(firstName, emailUser, password);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        try {
            EmployeeDTO employee = authService.authenticate(email, password);
            if (employee != null) {
                if (!employee.isActive()) {
                    model.addAttribute("error", "Your account is deactivated. Please contact admin.");
                    return "login";
                }
                session.setAttribute("loggedInUser", employee);
                return redirectBasedOnRole(employee);
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home?logout";
    }

    private String redirectBasedOnRole(EmployeeDTO employee) {
        if ("ADMIN".equalsIgnoreCase(employee.getRole())) {
            return "redirect:/admin/dashboard";
        } else if ("MANAGER".equalsIgnoreCase(employee.getRole())) {
            return "redirect:/manager/dashboard";
        } else {
            return "redirect:/employee/dashboard";
        }
    }
}
