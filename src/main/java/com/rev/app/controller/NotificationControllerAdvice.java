package com.rev.app.controller;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.service.EmployeeNotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificationControllerAdvice {

    @Autowired
    private EmployeeNotificationService notificationService;

    @ModelAttribute
    public void addUnreadCount(jakarta.servlet.http.HttpServletRequest request, HttpSession session, Model model) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof EmployeeDTO user && user.getId() != null) {
            long count = notificationService.getUnreadNotificationCount(user.getId());
            model.addAttribute("unreadCount", count);
        } else {
            model.addAttribute("unreadCount", 0L);
        }
        model.addAttribute("currentUri", request.getRequestURI());
    }
}
