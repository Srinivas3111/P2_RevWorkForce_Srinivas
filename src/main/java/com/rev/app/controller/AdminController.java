package com.rev.app.controller;

import com.rev.app.entity.Employee;
import com.rev.app.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    private boolean isAdmin(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("loggedInUser");
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("user", user);
            return true;
        }
        return false;
    }

    @GetMapping("/add-employee")
    public String showAddEmployeeForm(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        Employee newEmp = new Employee();
        newEmp.setId(employeeService.generateUniqueEmployeeId());
        model.addAttribute("employee", newEmp);
        model.addAttribute("managers", employeeService.getManagers());
        return "add_employee";
    }

    @PostMapping("/add-employee")
    public String addEmployee(@ModelAttribute Employee employee,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            // Check for required fields manually or using @Valid if integrated
            if (employee.getId() == null) {
                throw new Exception("Employee ID is required");
            }
            if (employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
                throw new Exception("First name is required");
            }
            if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
                throw new Exception("Last name is required");
            }
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                throw new Exception("Email is required");
            }

            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Employee added successfully!");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add-employee";
        }
    }

    @GetMapping("/employees")
    public String listEmployees(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employee_list";
    }

    @GetMapping("/edit-employee/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            model.addAttribute("error", "Employee not found");
            return "redirect:/admin/employees";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("managers", employeeService.getManagers());
        return "edit_employee";
    }

    @PostMapping("/edit-employee")
    public String updateEmployee(@ModelAttribute Employee employee,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            // Validation (mostly similar to add, but we check if ID exists)
            if (employee.getId() == null)
                throw new Exception("Employee ID is missing");

            Employee existing = employeeService.getEmployeeById(employee.getId());
            if (existing == null)
                throw new Exception("Employee record does not exist");

            // Keep existing password if not provided in form (security)
            if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
                employee.setPassword(existing.getPassword());
            }

            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/edit-employee/" + employee.getId();
        }
    }
}
