package com.rev.app.controller;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.service.EmployeeNotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {

    @Autowired
    private EmployeeNotificationService notificationService;

    @GetMapping("/notifications")
    public String notificationCenter(HttpSession session, Model model) {
        EmployeeDTO user = getLoggedInUser(session);
        if (user == null || !user.isActive()) {
            return "redirect:/";
        }

        var notifications = notificationService.getAllNotifications(user.getId());
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();

        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadNotificationCount", unreadCount);
        model.addAttribute("dashboardPath", resolveDashboardPath(user.getRole()));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id,
            @RequestParam(required = false) String redirect,
            HttpSession session) {
        EmployeeDTO user = getLoggedInUser(session);
        if (user == null || !user.isActive()) {
            return "redirect:/";
        }

        try {
            notificationService.markNotificationAsRead(user.getId(), id);
        } catch (Exception ignored) {
            // Notification read should not break user flow.
        }

        return "redirect:" + resolveRedirectPath(redirect);
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(@RequestParam(required = false) String redirect, HttpSession session) {
        EmployeeDTO user = getLoggedInUser(session);
        if (user == null || !user.isActive()) {
            return "redirect:/";
        }

        notificationService.markAllNotificationsAsRead(user.getId());
        return "redirect:" + resolveRedirectPath(redirect);
    }

    private EmployeeDTO getLoggedInUser(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        if (user instanceof EmployeeDTO) {
            return (EmployeeDTO) user;
        }
        return null;
    }

    private String resolveDashboardPath(String role) {
        if (role == null) {
            return "/";
        }
        String cleanRole = role.trim().toUpperCase();
        if ("ADMIN".equals(cleanRole)) {
            return "/admin/dashboard";
        }
        if ("MANAGER".equals(cleanRole)) {
            return "/manager/dashboard";
        }
        return "/employee/dashboard";
    }

    private String resolveRedirectPath(String redirect) {
        if (redirect == null || redirect.trim().isEmpty()) {
            return "/notifications";
        }
        String clean = redirect.trim();
        if (!clean.startsWith("/") || clean.startsWith("//")) {
            return "/notifications";
        }
        return clean;
    }
}
